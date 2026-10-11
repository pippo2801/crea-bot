package com.pippo2801.universalgamebot.core

import com.pippo2801.universalgamebot.verification.AutonomyCertificate

enum class SafetyState {
    GREEN, YELLOW, RED
}

/**
 * Fail-closed gate. A clear screenshot is not sufficient to authorize game input:
 * the selected game's exact profile must also have a complete successful certificate.
 */
class SafetyManager {
    var currentSafetyState: SafetyState = SafetyState.RED
        private set

    private var verifiedGameId: String? = null
    private var verifiedProfileVersion: String? = null

    fun evaluateSafety(confidence: Float, isUnknownState: Boolean, hasError: Boolean): SafetyState {
        currentSafetyState = when {
            hasError || isUnknownState || !confidence.isFinite() ||
                confidence < 0f || confidence > 1f || confidence < 0.4f -> SafetyState.RED
            confidence < 0.7f -> SafetyState.YELLOW
            else -> SafetyState.GREEN
        }
        if (currentSafetyState != SafetyState.GREEN) {
            clearVerifiedGame()
        }
        return currentSafetyState
    }

    fun authorizeVerifiedGame(
        expectedGameId: String,
        expectedProfileVersion: String,
        certificate: AutonomyCertificate
    ): Boolean {
        val valid = expectedGameId.isNotBlank() &&
            expectedProfileVersion.isNotBlank() &&
            certificate.gameId == expectedGameId &&
            certificate.profileVersion == expectedProfileVersion &&
            certificate.issuedAt > 0L &&
            certificate.totalTests > 0 &&
            certificate.passedTests == certificate.totalTests &&
            certificate.failedTests == 0 &&
            certificate.durationMs >= 0L &&
            certificate.confidence >= 1.0f &&
            certificate.confidence.isFinite() &&
            certificate.isVerified

        if (!valid) {
            clearVerifiedGame()
            return false
        }

        verifiedGameId = expectedGameId
        verifiedProfileVersion = expectedProfileVersion
        return true
    }

    fun clearVerifiedGame() {
        verifiedGameId = null
        verifiedProfileVersion = null
    }

    fun isActionAllowed(): Boolean =
        currentSafetyState == SafetyState.GREEN &&
            !verifiedGameId.isNullOrBlank() &&
            !verifiedProfileVersion.isNullOrBlank()
}
