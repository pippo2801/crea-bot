package com.pippo2801.universalgamebot.core

enum class SafetyState {
    GREEN, YELLOW, RED
}

class SafetyManager {

    // Fail closed: no action is permitted until a real observation is evaluated.
    var currentSafetyState: SafetyState = SafetyState.RED
        private set

    fun evaluateSafety(confidence: Float, isUnknownState: Boolean, hasError: Boolean): SafetyState {
        currentSafetyState = when {
            hasError || isUnknownState || !confidence.isFinite() ||
                confidence < 0f || confidence > 1f || confidence < 0.4f -> SafetyState.RED
            confidence < 0.7f -> SafetyState.YELLOW
            else -> SafetyState.GREEN
        }
        return currentSafetyState
    }

    fun isActionAllowed(): Boolean = currentSafetyState == SafetyState.GREEN
}
