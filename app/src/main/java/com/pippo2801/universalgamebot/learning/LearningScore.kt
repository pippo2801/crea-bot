package com.pippo2801.universalgamebot.learning

import com.pippo2801.universalgamebot.games.AutonomyStatus

data class LearningScoreBreakdown(
    val screenRecognition: Int = 10,
    val objectRecognition: Int = 10,
    val controls: Int = 10,
    val rules: Int = 10,
    val objective: Int = 10,
    val stateRecognition: Int = 10,
    val strategy: Int = 10,
    val reactions: Int = 10,
    val errorHandling: Int = 10,
    val autonomousTests: Int = 10
) {
    val totalScore: Int
        get() = (screenRecognition + objectRecognition + controls + rules +
                objective + stateRecognition + strategy + reactions +
                errorHandling + autonomousTests).coerceIn(0, 100)

    val autonomyStatus: AutonomyStatus
        get() = when (totalScore) {
            in 0..39 -> AutonomyStatus.OBSERVATION
            in 40..69 -> AutonomyStatus.LEARNING
            in 70..89 -> AutonomyStatus.AUTONOMOUS_TESTING
            in 90..99 -> AutonomyStatus.NEARLY_READY
            100 -> AutonomyStatus.AUTONOMY_VERIFIED
            else -> AutonomyStatus.OBSERVATION
        }
}
