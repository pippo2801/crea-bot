package com.pippo2801.universalgamebot.ui

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import com.pippo2801.universalgamebot.core.*
import com.pippo2801.universalgamebot.games.GameRegistry
import com.pippo2801.universalgamebot.diagnostics.DiagnosticsManager
import com.pippo2801.universalgamebot.service.BotAccessibilityService
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

    fun runDiagnostics() = diagnosticsManager.runDiagnostics(
        hasAccessibility = isAccessibilityServiceEnabled(),
        hasScreenCapture = screenCaptureManager.isReady,
        isObservationRunning = isBotRunning.value
    )

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expected = ComponentName(getApplication(), BotAccessibilityService::class.java)
            .flattenToString()
        val enabled = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return TextUtils.SimpleStringSplitter(':').run {
            setString(enabled)
            any { it.equals(expected, ignoreCase = true) }
        }
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
