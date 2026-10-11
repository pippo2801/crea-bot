package com.pippo2801.universalgamebot.learning

import com.pippo2801.universalgamebot.games.AutonomyStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class LearningScoreTest {

    @Test
    fun defaultBreakdownStartsWithNoUnverifiedProgress() {
        val score = LearningScoreBreakdown()

        assertEquals(0, score.totalScore)
        assertEquals(AutonomyStatus.OBSERVATION, score.autonomyStatus)
    }

    @Test
    fun scoreTiersNeverCertifyAutonomyByScoreAlone() {
        assertEquals(AutonomyStatus.OBSERVATION, score(39).autonomyStatus)
        assertEquals(AutonomyStatus.LEARNING, score(40).autonomyStatus)
        assertEquals(AutonomyStatus.LEARNING, score(69).autonomyStatus)
        assertEquals(AutonomyStatus.AUTONOMOUS_TESTING, score(70).autonomyStatus)
        assertEquals(AutonomyStatus.AUTONOMOUS_TESTING, score(89).autonomyStatus)
        assertEquals(AutonomyStatus.NEARLY_READY, score(90).autonomyStatus)
        assertEquals(AutonomyStatus.NEARLY_READY, score(99).autonomyStatus)
        assertEquals(AutonomyStatus.NEARLY_READY, score(100).autonomyStatus)
    }

    @Test
    fun eachDimensionIsClampedToItsTenPointRange() {
        assertEquals(0, LearningScoreBreakdown(
            screenRecognition = -20
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

    private fun score(target: Int): LearningScoreBreakdown =
        LearningScoreBreakdown(screenRecognition = target)
}
