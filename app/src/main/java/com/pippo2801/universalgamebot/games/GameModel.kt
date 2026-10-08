package com.pippo2801.universalgamebot.games

enum class AutonomyStatus {
    OBSERVATION,
    LEARNING,
    AUTONOMOUS_TESTING,
    NEARLY_READY,
    AUTONOMY_VERIFIED,
    NOT_AUTONOMOUS
}

data class GameProfile(
    val gameId: String,
    val name: String,
    val version: String = "1.0.0",
    val learningScore: Int = 0,
    val confidence: Float = 0.0f,
    val autonomyStatus: AutonomyStatus = AutonomyStatus.OBSERVATION,
    val lastVerifiedAt: Long = 0L,
    val strategiesCount: Int = 0,
    val errorCount: Int = 0,
    val isVerified: Boolean = false
)
