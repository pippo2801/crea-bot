package com.pippo2801.universalgamebot.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pippo2801.universalgamebot.core.*
import com.pippo2801.universalgamebot.games.GameRegistry
import com.pippo2801.universalgamebot.diagnostics.DiagnosticsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val screenCaptureManager = ScreenCaptureManager(application)
    val visionEngine = VisionEngine()
    val actionEngine = ActionEngine()
    val safetyManager = SafetyManager()
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

    fun startBot() {
        botLoop.start()
    }

    fun stopBot() {
        botLoop.stop()
    }
}
