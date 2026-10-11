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
 * Actions are fail-closed. GREEN observation alone is insufficient: SafetyManager
 * also requires a matching, fully successful game-autonomy certificate.
 */
class ActionEngine(private val safetyManager: SafetyManager? = null) {

    fun executeAction(action: BotAction): Boolean {
        if (action.type == ActionType.NONE) return true

        if (safetyManager?.isActionAllowed() != true) {
            Log.w("ActionEngine", "Blocked action: safety authorization missing: ${action.description}")
            return false
        }

        if (action.type != ActionType.BACK) {
            val coordinates = if (action.type == ActionType.TAP || action.type == ActionType.LONG_PRESS) {
                listOf(action.x1, action.y1)
            } else {
                listOf(action.x1, action.y1, action.x2, action.y2)
            }
            if (coordinates.any { !it.isFinite() || it < 0f } || action.duration !in 1L..5000L) {
                Log.w("ActionEngine", "Blocked action with invalid coordinates or duration: ${action.description}")
                return false
            }
        }

        val service = BotAccessibilityService.instance
        if (service == null) {
            Log.w("ActionEngine", "Accessibility service is not active: ${action.description}")
            return false
        }

        val accepted = when (action.type) {
            ActionType.TAP -> service.performTap(action.x1, action.y1, null)
            ActionType.SWIPE, ActionType.DRAG ->
                service.performSwipe(action.x1, action.y1, action.x2, action.y2, action.duration, null)
            ActionType.LONG_PRESS ->
                service.performSwipe(action.x1, action.y1, action.x1, action.y1, 800L, null)
            ActionType.BACK -> service.performGlobalAction(
                android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK
            )
            ActionType.NONE -> true
        }

        if (!accepted) {
            Log.w("ActionEngine", "Android rejected action dispatch: ${action.description}")
            return false
        }
        Log.i("ActionEngine", "Action dispatch accepted: ${action.type} (${action.description})")
        return true
    }
}
