package com.pippo2801.universalgamebot.core

enum class SafetyState {
    GREEN, YELLOW, RED
}

class SafetyManager {

    var currentSafetyState: SafetyState = SafetyState.GREEN
        private set

    fun evaluateSafety(confidence: Float, isUnknownState: Boolean, hasError: Boolean): SafetyState {
        currentSafetyState = when {
            hasError || isUnknownState || confidence < 0.4f -> SafetyState.RED
            confidence < 0.7f -> SafetyState.YELLOW
            else -> SafetyState.GREEN
        }
        return currentSafetyState
    }

    fun isActionAllowed(): Boolean {
        return currentSafetyState == SafetyState.GREEN
    }
}
