package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun AiResourcesScreen(viewModel: MainViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI e risorse", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Modalità locale gratuita", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "L'app non richiede chiavi API a pagamento. Al momento non è configurato un router verso servizi AI esterni: il riconoscimento visivo e le strategie di gioco sono ancora da implementare.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Nessun servizio cloud viene contattato da questa schermata. Le opzioni AI online saranno mostrate solo quando sarà disponibile una configurazione reale.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
