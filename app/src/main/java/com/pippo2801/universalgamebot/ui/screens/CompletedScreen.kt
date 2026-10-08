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
fun CompletedScreen(viewModel: MainViewModel) {
    val verifiedProfiles = viewModel.gameRegistry.getAllProfiles().filter { it.isVerified }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Autonomy Verified Games (100%)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (verifiedProfiles.isEmpty()) {
            Text("No games have achieved 100% verified autonomy yet. Run autonomous tests to verify games.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(verifiedProfiles) { profile ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(profile.name, style = MaterialTheme.typography.titleMedium)
                            Text("Autonomy Certificate Valid", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
