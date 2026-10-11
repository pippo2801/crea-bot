package com.pippo2801.universalgamebot.verification

/**
 * A concrete check that can be run by the verification engine.
 * Checks should validate observable behavior, not merely return a preset result.
 */
data class VerificationCheck(
    val name: String,
    val run: () -> Boolean
)

class VerificationEngine(
    private val checks: List<VerificationCheck> = emptyList()
) {
    fun runAutonomousTest(gameId: String, profileVersion: String): AutonomyCertificate {
        val startedAt = System.currentTimeMillis()
        val results = checks.map { check ->
            runCatching { check.run() }.getOrDefault(false)
        }
        val passed = results.count { it }
        val total = results.size
        val failed = total - passed
        val confidence = if (total == 0) 0f else passed.toFloat() / total.toFloat()

        return AutonomyCertificate(
            gameId = gameId,
            profileVersion = profileVersion,
            issuedAt = System.currentTimeMillis(),
            totalTests = total,
            passedTests = passed,
            failedTests = failed,
            durationMs = (System.currentTimeMillis() - startedAt).coerceAtLeast(0L),
            confidence = confidence,
            // No checks means no evidence; a certificate is issued only when every
            // configured check actually ran and passed.
            isVerified = total > 0 && failed == 0
        )
    }
}
