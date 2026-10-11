package com.pippo2801.universalgamebot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafetyManagerTest {

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
    fun onlyHighConfidenceKnownStateAllowsActions() {
        val safety = SafetyManager()

        assertEquals(SafetyState.GREEN, safety.evaluateSafety(0.9f, false, false))
        assertTrue(safety.isActionAllowed())
    }
}
