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
        _currentStatus.value = "Avvio osservazione"
        botJob = scope.launch {
            while (isActive && _isRunning.value) {
                try {
                    val bitmap = screenCaptureManager.captureLatestBitmap()
                    if (bitmap == null) {
                        safetyManager.evaluateSafety(0.0f, true, true)
                        _confidence.value = 0.0f
                        _currentStatus.value = "Acquisizione schermo non disponibile: azioni bloccate"
                        delay(1000)
                        continue
                    }
                    val visionResult = visionEngine.analyzeBitmap(bitmap)
                    _confidence.value = visionResult.confidence
                    val gameName = gameDetector.detectGame(visionResult)
                    val safety = safetyManager.evaluateSafety(
                        visionResult.confidence, gameName == "Unknown Game", false
                    )
                    if (safety != SafetyState.GREEN) {
                        _currentStatus.value = "Solo osservazione: gioco o schermata non verificati"
                        delay(1500)
                        continue
                    }
                    _currentStatus.value = "Riconosciuto: $gameName"
                    // Gameplay remains disabled until a validated game adapter is connected.
                    delay(1000)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("BotLoop", "Errore nel ciclo di osservazione", e)
                    safetyManager.evaluateSafety(0.0f, true, true)
                    _currentStatus.value = "Errore: ${e.message ?: "sconosciuto"}; azioni bloccate"
                    delay(2000)
                }
            }
        }
    }

    fun reportStatus(message: String) {
        _currentStatus.value = message
    }

    fun stop() {
        _isRunning.value = false
        botJob?.cancel()
        botJob = null
        _currentStatus.value = "Fermato dall'utente"
        Log.i("BotLoop", "Ciclo del bot fermato.")
    }
}
