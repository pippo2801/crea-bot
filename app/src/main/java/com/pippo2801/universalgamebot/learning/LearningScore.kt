package com.pippo2801.universalgamebot.learning

import com.pippo2801.universalgamebot.games.AutonomyStatus

data class LearningScoreBreakdown(
    val screenRecognition: Int = 0,
    val objectRecognition: Int = 0,
    val controls: Int = 0,
    val rules: Int = 0,
    val objective: Int = 0,
    val stateRecognition: Int = 0,
    val strategy: Int = 0,
    val reactions: Int = 0,
    val errorHandling: Int = 0,
    val autonomousTests: Int = 0
) {
    val totalScore: Int
        get() = (screenRecognition.coerceIn(0, 10) +
                objectRecognition.coerceIn(0, 10) +
                controls.coerceIn(0, 10) +
                rules.coerceIn(0, 10) +
                objective.coerceIn(0, 10) +
                stateRecognition.coerceIn(0, 10) +
                strategy.coerceIn(0, 10) +
                reactions.coerceIn(0, 10) +
                errorHandling.coerceIn(0, 10) +
                autonomousTests.coerceIn(0, 10)).coerceIn(0, 100)

    /**
     * A learning score alone must never certify autonomy.
     * AUTONOMY_VERIFIED is reserved for successful real verification checks.
     */
    val autonomyStatus: AutonomyStatus
        get() = when (totalScore) {
            in 0..39 -> AutonomyStatus.OBSERVATION
            in 40..69 -> AutonomyStatus.LEARNING
            in 70..89 -> AutonomyStatus.AUTONOMOUS_TESTING
            in 90..100 -> AutonomyStatus.NEARLY_READY
            else -> AutonomyStatus.OBSERVATION
        }
}
