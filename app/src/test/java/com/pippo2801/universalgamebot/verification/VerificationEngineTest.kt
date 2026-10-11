package com.pippo2801.universalgamebot.verification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VerificationEngineTest {

    @Test
    fun emptyCheckSuiteDoesNotIssueCertificate() {
        val certificate = VerificationEngine().runAutonomousTest("demo", "1")

        assertEquals(0, certificate.totalTests)
        assertEquals(0, certificate.passedTests)
        assertEquals(0, certificate.failedTests)
        assertEquals(0f, certificate.confidence, 0.0001f)
        assertFalse(certificate.isVerified)
    }

    @Test
    fun allPassingChecksIssueCertificate() {
        val engine = VerificationEngine(
            listOf(
                VerificationCheck("screen", { true }),
                VerificationCheck("safe action", { true })
            )
        )

        val certificate = engine.runAutonomousTest("demo", "1")

        assertEquals(2, certificate.totalTests)
        assertEquals(2, certificate.passedTests)
        assertEquals(0, certificate.failedTests)
        assertEquals(1f, certificate.confidence, 0.0001f)
        assertTrue(certificate.isVerified)
    }

    @Test
    fun failedCheckPreventsCertificate() {
        val engine = VerificationEngine(
            listOf(
                VerificationCheck("screen", { true }),
                VerificationCheck("victory", { false })
            )
        )

        val certificate = engine.runAutonomousTest("demo", "1")

        assertEquals(2, certificate.totalTests)
        assertEquals(1, certificate.passedTests)
        assertEquals(1, certificate.failedTests)
        assertEquals(0.5f, certificate.confidence, 0.0001f)
        assertFalse(certificate.isVerified)
    }

    @Test
    fun throwingCheckCountsAsFailure() {
        val engine = VerificationEngine(
            listOf(VerificationCheck("throws", { error("test failure") }))
        )

        val certificate = engine.runAutonomousTest("demo", "1")

        assertEquals(1, certificate.totalTests)
        assertEquals(0, certificate.passedTests)
        assertEquals(1, certificate.failedTests)
        assertFalse(certificate.isVerified)
    }
}
