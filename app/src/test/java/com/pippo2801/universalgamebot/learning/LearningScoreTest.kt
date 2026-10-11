package com.pippo2801.universalgamebot.learning

import com.pippo2801.universalgamebot.games.AutonomyStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class LearningScoreTest {

    @Test
    fun defaultBreakdownIsFullyScored() {
        val score = LearningScoreBreakdown()

        assertEquals(100, score.totalScore)
        assertEquals(AutonomyStatus.AUTONOMY_VERIFIED, score.autonomyStatus)
    }

    @Test
    fun scoreTiersUseDocumentedBoundaries() {
        assertEquals(AutonomyStatus.OBSERVATION, score(39).autonomyStatus)
        assertEquals(AutonomyStatus.LEARNING, score(40).autonomyStatus)
        assertEquals(AutonomyStatus.LEARNING, score(69).autonomyStatus)
        assertEquals(AutonomyStatus.AUTONOMOUS_TESTING, score(70).autonomyStatus)
        assertEquals(AutonomyStatus.AUTONOMOUS_TESTING, score(89).autonomyStatus)
        assertEquals(AutonomyStatus.NEARLY_READY, score(90).autonomyStatus)
        assertEquals(AutonomyStatus.NEARLY_READY, score(99).autonomyStatus)
        assertEquals(AutonomyStatus.AUTONOMY_VERIFIED, score(100).autonomyStatus)
    }

    @Test
    fun totalScoreIsClampedToRange() {
        assertEquals(0, LearningScoreBreakdown(
            screenRecognition = -20,
            objectRecognition = 0,
            controls = 0,
            rules = 0,
            objective = 0,
            stateRecognition = 0,
            strategy = 0,
            reactions = 0,
            errorHandling = 0,
            autonomousTests = 0
        ).totalScore)

        assertEquals(100, LearningScoreBreakdown(
            screenRecognition = 20,
            objectRecognition = 20,
            controls = 20,
            rules = 20,
            objective = 20,
            stateRecognition = 20,
            strategy = 20,
            reactions = 20,
            errorHandling = 20,
            autonomousTests = 20
        ).totalScore)
    }

    private fun score(target: Int): LearningScoreBreakdown {
        // Ten dimensions share the target evenly; use one dimension for boundaries
        // and zero for the rest to exercise the exact aggregate score.
        return LearningScoreBreakdown(
            screenRecognition = target,
            objectRecognition = 0,
            controls = 0,
            rules = 0,
            objective = 0,
            stateRecognition = 0,
            strategy = 0,
            reactions = 0,
            errorHandling = 0,
            autonomousTests = 0
        )
    }
}
