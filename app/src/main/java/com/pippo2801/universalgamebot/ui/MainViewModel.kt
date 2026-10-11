package com.pippo2801.universalgamebot.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.pippo2801.universalgamebot.core.*
import com.pippo2801.universalgamebot.games.GameRegistry
import com.pippo2801.universalgamebot.diagnostics.DiagnosticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val screenCaptureManager = ScreenCaptureManager(application)
    val visionEngine = VisionEngine()
    val safetyManager = SafetyManager()
    val actionEngine = ActionEngine(safetyManager)
    val gameDetector = GameDetector()
    val botLoop = BotLoop(screenCaptureManager, visionEngine, actionEngine, safetyManager, gameDetector)
    val gameRegistry = GameRegistry()
    val diagnosticsManager = DiagnosticsManager()

    val isBotRunning = botLoop.isRunning
    val botStatus = botLoop.currentStatus
    val confidence = botLoop.confidence

    private val _selectedGameId = MutableStateFlow<String?>(null)
    val selectedGameId: StateFlow<String?> = _selectedGameId

    fun selectGame(gameId: String) {
        _selectedGameId.value = gameId
    }

    fun startBot(resultCode: Int, data: Intent, width: Int, height: Int, densityDpi: Int) {
        try {
            screenCaptureManager.initProjection(resultCode, data, width, height, densityDpi)
            botLoop.start()
        } catch (e: Exception) {
            botLoop.reportStatus("Impossibile avviare acquisizione schermo: ${e.message ?: "errore"}")
            screenCaptureManager.release()
        }
    }

    fun stopBot() {
        botLoop.stop()
        screenCaptureManager.release()
    }

    override fun onCleared() {
        botLoop.stop()
        screenCaptureManager.release()
        super.onCleared()
    }
}
