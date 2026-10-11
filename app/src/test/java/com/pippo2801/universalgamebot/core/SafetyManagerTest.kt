package com.pippo2801.universalgamebot.core

import com.pippo2801.universalgamebot.verification.AutonomyCertificate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyManagerTest {
    private fun validCertificate() = AutonomyCertificate(
        gameId = "test_game",
        profileVersion = "1",
        issuedAt = 100L,
        totalTests = 10,
        passedTests = 10,
        failedTests = 0,
        durationMs = 5000L,
        confidence = 1.0f,
        isVerified = true
    )

    @Test
    fun startsInRedAndBlocksActions() {
        val safety = SafetyManager()
        assertEquals(SafetyState.RED, safety.currentSafetyState)
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun unknownStateOrErrorAlwaysStops() {
        val safety = SafetyManager()
        assertEquals(SafetyState.RED, safety.evaluateSafety(0.99f, true, false))
        assertFalse(safety.isActionAllowed())
        assertEquals(SafetyState.RED, safety.evaluateSafety(0.99f, false, true))
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun invalidConfidenceAlwaysStops() {
        val safety = SafetyManager()
        assertEquals(SafetyState.RED, safety.evaluateSafety(Float.NaN, false, false))
        assertEquals(SafetyState.RED, safety.evaluateSafety(Float.POSITIVE_INFINITY, false, false))
        assertEquals(SafetyState.RED, safety.evaluateSafety(-0.1f, false, false))
        assertEquals(SafetyState.RED, safety.evaluateSafety(1.1f, false, false))
    }

    @Test
    fun mediumConfidenceDoesNotAllowActions() {
        val safety = SafetyManager()
        assertEquals(SafetyState.YELLOW, safety.evaluateSafety(0.6f, false, false))
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun greenScreenWithoutVerifiedGameStillBlocksActions() {
        val safety = SafetyManager()
        assertEquals(SafetyState.GREEN, safety.evaluateSafety(0.9f, false, false))
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun validCertificateAndGreenObservationAreBothRequired() {
        val safety = SafetyManager()
        assertTrue(safety.authorizeVerifiedGame("test_game", "1", validCertificate()))
        assertFalse(safety.isActionAllowed())
        assertEquals(SafetyState.GREEN, safety.evaluateSafety(0.9f, false, false))
        assertTrue(safety.isActionAllowed())
    }

    @Test
    fun certificateForDifferentProfileIsRejected() {
        val safety = SafetyManager()
        assertFalse(safety.authorizeVerifiedGame("other_game", "1", validCertificate()))
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun invalidCertificateIsRejected() {
        val safety = SafetyManager()
        val invalid = validCertificate().copy(failedTests = 1)
        assertFalse(safety.authorizeVerifiedGame("test_game", "1", invalid))
        assertFalse(safety.isActionAllowed())
    }

    @Test
    fun losingGreenStateClearsAuthorization() {
        val safety = SafetyManager()
        safety.authorizeVerifiedGame("test_game", "1", validCertificate())
        safety.evaluateSafety(0.9f, false, false)
        assertTrue(safety.isActionAllowed())
        safety.evaluateSafety(0.2f, false, false)
        assertFalse(safety.isActionAllowed())
    }
}
