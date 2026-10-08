package com.pippo2801.universalgamebot.verification

data class AutonomyCertificate(
    val gameId: String,
    val profileVersion: String,
    val issuedAt: Long,
    val totalTests: Int,
    val passedTests: Int,
    val failedTests: Int,
    val durationMs: Long,
    val confidence: Float,
    val isVerified: Boolean
)
