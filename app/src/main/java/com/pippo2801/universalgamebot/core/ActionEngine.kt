package com.pippo2801.universalgamebot.core

import android.util.Log
import com.pippo2801.universalgamebot.service.BotAccessibilityService

enum class ActionType {
    TAP, SWIPE, DRAG, LONG_PRESS, BACK, NONE
}

data class BotAction(
    val type: ActionType,
    val x1: Float = 0f,
    val y1: Float = 0f,
    val x2: Float = 0f,
    val y2: Float = 0f,
    val duration: Long = 100L,
    val description: String = ""
)

/**
 * Actions are fail-closed: callers must inject the same SafetyManager used to evaluate
 * the current screen. Without explicit safety authorization, only NONE is accepted.
 */
class ActionEngine(
    private val safetyManager: SafetyManager? = null
) {

    fun executeAction(action: BotAction): Boolean {
        if (action.type == ActionType.NONE) return true

        if (safetyManager?.isActionAllowed() != true) {
            Log.w("ActionEngine", "Blocked action because safety authorization is missing or not GREEN: " + action.description)
            return false
        }

        if (action.type != ActionType.BACK) {
            val coordinates = listOf(action.x1, action.y1, action.x2, action.y2)
            if (coordinates.any { !it.isFinite() || it < 0f } ||
                action.duration !in 1L..5000L
            ) {
                Log.w("ActionEngine", "Blocked action with invalid coordinates or duration: " + action.description)
                return false
            }
        }

        val accessibilityService = BotAccessibilityService.instance
        if (accessibilityService == null) {
            Log.w("ActionEngine", "Accessibility service is not active. Cannot perform action: " + action.description)
            return false
        }

        when (action.type) {
            ActionType.TAP -> {
                accessibilityService.performTap(action.x1, action.y1, null)
                Log.i("ActionEngine", "Performed TAP at (" + action.x1 + ", " + action.y1 + ")")
            }
            ActionType.SWIPE, ActionType.DRAG -> {
                accessibilityService.performSwipe(action.x1, action.y1, action.x2, action.y2, action.duration, null)
                Log.i("ActionEngine", "Performed SWIPE from (" + action.x1 + ", " + action.y1 + ") to (" + action.x2 + ", " + action.y2 + ")")
            }
            ActionType.LONG_PRESS -> {
                accessibilityService.performSwipe(action.x1, action.y1, action.x1, action.y1, 800L, null)
                Log.i("ActionEngine", "Performed LONG_PRESS at (" + action.x1 + ", " + action.y1 + ")")
            }
            ActionType.BACK -> {
                accessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                Log.i("ActionEngine", "Performed GLOBAL_ACTION_BACK")
            }
            ActionType.NONE -> return true
        }
        return true
    }
}
