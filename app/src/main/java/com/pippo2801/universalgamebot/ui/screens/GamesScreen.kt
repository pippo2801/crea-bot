package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun GamesScreen(viewModel: MainViewModel, onNavigateToDetails: (String) -> Unit) {
    val profiles = viewModel.gameRegistry.getAllProfiles()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Registered Games (${profiles.size})", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profiles) { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectGame(profile.gameId)
                            onNavigateToDetails(profile.gameId)
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(profile.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Score: ${profile.learningScore}% | Status: ${profile.autonomyStatus}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
