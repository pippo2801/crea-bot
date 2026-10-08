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

class ActionEngine {

    fun executeAction(action: BotAction): Boolean {
        val accessibilityService = BotAccessibilityService.instance
        if (accessibilityService == null) {
            Log.w("ActionEngine", "Accessibility service is not active. Cannot perform action: ${action.description}")
            return false
        }

        when (action.type) {
            ActionType.TAP -> {
                accessibilityService.performTap(action.x1, action.y1, null)
                Log.i("ActionEngine", "Performed TAP at (${action.x1}, ${action.y1})")
                return true
            }
            ActionType.SWIPE, ActionType.DRAG -> {
                accessibilityService.performSwipe(action.x1, action.y1, action.x2, action.y2, action.duration, null)
                Log.i("ActionEngine", "Performed SWIPE from (${action.x1}, ${action.y1}) to (${action.x2}, ${action.y2})")
                return true
            }
            ActionType.LONG_PRESS -> {
                accessibilityService.performSwipe(action.x1, action.y1, action.x1, action.y1, 800L, null)
                Log.i("ActionEngine", "Performed LONG_PRESS at (${action.x1}, ${action.y1})")
                return true
            }
            ActionType.BACK -> {
                accessibilityService.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                Log.i("ActionEngine", "Performed GLOBAL_ACTION_BACK")
                return true
            }
            ActionType.NONE -> {
                return true
            }
        }
    }
}
