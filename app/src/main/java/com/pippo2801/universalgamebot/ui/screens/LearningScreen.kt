package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun LearningScreen(viewModel: MainViewModel) {
    val profiles = viewModel.gameRegistry.getAllProfiles()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Learning Center", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(profiles) { profile ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(profile.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { profile.learningScore / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Learning Score: ${profile.learningScore}% [${profile.autonomyStatus}]", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { /* Start Learning */ }) {
                                Text("Learn")
                            }
                            OutlinedButton(onClick = { /* Autonomous Test */ }) {
                                Text("Test")
                            }
                        }
                    }
                }
            }
        }
    }
}
