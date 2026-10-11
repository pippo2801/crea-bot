package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.core.SafetyState
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel, onStart: () -> Unit, onStop: () -> Unit) {
    val isRunning by viewModel.isBotRunning.collectAsState()
    val status by viewModel.botStatus.collectAsState()
    val confidence by viewModel.confidence.collectAsState()
    val safety = viewModel.safetyManager.currentSafetyState

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Universal Game Bot", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Framework Android offline-first · modalità osservazione", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Stato: $status", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Confidenza riconoscimento: ${(confidence * 100).toInt()}%")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Sicurezza: $safety",
                    color = when (safety) {
                        SafetyState.GREEN -> Color(0xFF2E7D32)
                        SafetyState.YELLOW -> Color(0xFFF9A825)
                        SafetyState.RED -> MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Le azioni automatiche restano bloccate finché gioco e schermata non sono verificati.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = onStart, enabled = !isRunning) { Text("AVVIA OSSERVAZIONE") }
            OutlinedButton(onClick = onStop, enabled = isRunning) { Text("FERMA") }
        }
    }
}
