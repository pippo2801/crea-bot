package com.pippo2801.universalgamebot.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class BotAccessibilityService : AccessibilityService() {

    companion object {
        var instance: BotAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i("BotAccessibilityService", "Accessibility Service connected successfully.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Observe accessibility events if needed for game state observation.
    }

    override fun onInterrupt() {
        Log.w("BotAccessibilityService", "Accessibility Service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
        Log.i("BotAccessibilityService", "Accessibility Service destroyed.")
    }

    fun performTap(x: Float, y: Float, callback: GestureResultCallback?) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, 50)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                callback?.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                callback?.onCancelled(gestureDescription)
            }
        }, null)
    }

    fun performSwipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long,
        callback: GestureResultCallback?
    ) {
        val safeDuration = duration.coerceIn(1L, 5000L)
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, safeDuration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                callback?.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                callback?.onCancelled(gestureDescription)
            }
        }, null)
    }
}
