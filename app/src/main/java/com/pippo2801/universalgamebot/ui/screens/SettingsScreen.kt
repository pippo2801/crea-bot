package com.pippo2801.universalgamebot.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Impostazioni e autorizzazioni", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Servizio Accessibilità", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Necessario per simulare tocchi e scorrimenti. Il bot attuale resta in osservazione e non esegue mosse automatiche.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }) {
                    Text("Apri impostazioni Accessibilità")
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "L'acquisizione dello schermo viene richiesta ogni volta che premi Avvia. Puoi revocare il permesso fermando il bot o dalle impostazioni Android.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
