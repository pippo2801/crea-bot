package com.pippo2801.universalgamebot.games

import com.pippo2801.universalgamebot.verification.AutonomyCertificate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRegistryTest {

    @Test
    fun registeredGamesStartUnverified() {
        val registry = GameRegistry()
        val profiles = registry.getAllProfiles()

        assertTrue(profiles.isNotEmpty())
        assertTrue(profiles.all { !it.isVerified })
        assertTrue(profiles.all { it.learningScore == 0 })
        assertTrue(profiles.all { it.autonomyStatus == AutonomyStatus.OBSERVATION })
    }

    @Test
    fun ordinaryUpdateCannotSetVerifiedFlag() {
        val registry = GameRegistry()
        val profile = registry.getProfile("ea_sports_fc_mobile")!!
        registry.updateProfile(
            profile.copy(
                isVerified = true,
                autonomyStatus = AutonomyStatus.AUTONOMY_VERIFIED,
                lastVerifiedAt = 123L,
                confidence = 1f
            )
        )

        val stored = registry.getProfile(profile.gameId)!!
        assertFalse(stored.isVerified)
        assertEquals(AutonomyStatus.OBSERVATION, stored.autonomyStatus)
        assertEquals(0L, stored.lastVerifiedAt)
    }

    @Test
    fun learningProgressCanAdvanceButNeverSelfCertifies() {
        val registry = GameRegistry()
        val profile = registry.getProfile("ea_sports_fc_mobile")!!
        registry.updateProfile(profile.copy(learningScore = 95))

        val stored = registry.getProfile(profile.gameId)!!
        assertEquals(95, stored.learningScore)
        assertEquals(AutonomyStatus.NEARLY_READY, stored.autonomyStatus)
        assertFalse(stored.isVerified)
        assertEquals(0L, stored.lastVerifiedAt)
    }

    @Test
    fun mismatchedCertificateCannotVerifyAnotherGame() {
        val registry = GameRegistry()
        val profile = registry.getProfile("ea_sports_fc_mobile")!!
        val certificate = certificate(gameId = "sudoku", version = profile.version)

        assertFalse(registry.applyCertificate(profile, certificate))
        assertFalse(registry.getProfile(profile.gameId)!!.isVerified)
    }

    @Test
    fun successfulMatchingCertificateVerifiesProfile() {
        val registry = GameRegistry()
        val profile = registry.getProfile("ea_sports_fc_mobile")!!
        val certificate = certificate(
            gameId = profile.gameId,
            version = profile.version
        )

        assertTrue(registry.applyCertificate(profile, certificate))
        val stored = registry.getProfile(profile.gameId)!!
        assertTrue(stored.isVerified)
        assertEquals(AutonomyStatus.AUTONOMY_VERIFIED, stored.autonomyStatus)
        assertEquals(certificate.issuedAt, stored.lastVerifiedAt)
        assertNotNull(stored)
    }

    private fun certificate(gameId: String, version: String) = AutonomyCertificate(
        gameId = gameId,
        profileVersion = version,
        issuedAt = 1234L,
        totalTests = 3,
        passedTests = 3,
        failedTests = 0,
        durationMs = 50L,
        confidence = 1f,
        isVerified = true
    )
}
