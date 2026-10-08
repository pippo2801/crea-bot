package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun GameDetailsScreen(viewModel: MainViewModel, gameId: String) {
    val profile = viewModel.gameRegistry.getProfile(gameId)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (profile == null) {
            Text("Game profile not found.", style = MaterialTheme.typography.headlineMedium)
        } else {
            Text("Game: ${profile.name}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Game ID: ${profile.gameId}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Version: ${profile.version}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Learning Score: ${profile.learningScore}%", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Autonomy Status: ${profile.autonomyStatus}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Strategies Recorded: ${profile.strategiesCount}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Errors Recorded: ${profile.errorCount}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
