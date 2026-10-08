package com.pippo2801.universalgamebot.core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BotLoop(
    private val screenCaptureManager: ScreenCaptureManager,
    private val visionEngine: VisionEngine,
    private val actionEngine: ActionEngine,
    private val safetyManager: SafetyManager,
    private val gameDetector: GameDetector
) {

    private var botJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentStatus = MutableStateFlow("Idle")
    val currentStatus: StateFlow<String> = _currentStatus

    private val _confidence = MutableStateFlow(0.0f)
    val confidence: StateFlow<Float> = _confidence

    fun start() {
        if (_isRunning.value) return
        _isRunning.value = true
        _currentStatus.value = "Running (Observe Loop)"

        botJob = scope.launch {
            while isActive && _isRunning.value {
                try {
                    // 1. OBSERVE
                    val bitmap = screenCaptureManager.captureLatestBitmap()
                    // 2. UNDERSTAND / VISION
                    val visionResult = visionEngine.analyzeBitmap(bitmap)
                    _confidence.value = visionResult.confidence

                    // 3. DETECT GAME
                    val gameName = gameDetector.detectGame(visionResult)

                    // 4. SAFETY EVALUATION
                    val safety = safetyManager.evaluateSafety(
                        visionResult.confidence,
                        gameName == "Unknown Game",
                        false
                    )

                    if (safety == SafetyState.RED) {
                        _currentStatus.value = "SAFETY STOP (RED): Unknown state or low confidence"
                        delay(2000)
                        continue
                    }

                    _currentStatus.value = "Active: $gameName [Safety: $safety, Conf: ${visionResult.confidence}]"

                    // Throttling for battery & performance optimization
                    delay(1000)
                } catch (e: Exception) {
                    Log.e("BotLoop", "Error in bot main loop", e)
                    _currentStatus.value = "Error: ${e.message}"
                    delay(2000)
                }
            }
        }
    }

    fun stop() {
        _isRunning.value = false
        botJob?.cancel()
        botJob = null
        _currentStatus.value = "Stopped by user"
        Log.i("BotLoop", "Bot loop stopped.")
    }
}
