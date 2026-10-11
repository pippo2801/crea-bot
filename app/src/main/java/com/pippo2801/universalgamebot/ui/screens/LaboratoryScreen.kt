package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.diagnostics.DiagnosticStatus
import com.pippo2801.universalgamebot.ui.MainViewModel

@Composable
fun LaboratoryScreen(viewModel: MainViewModel) {
    val isRunning by viewModel.isBotRunning.collectAsState()
    val diagnostics = viewModel.runDiagnostics(isRunning)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Diagnostics & Laboratory", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Stato rilevato dal dispositivo e funzioni realmente implementate. Gli avvisi non bloccanti indicano funzioni ancora mancanti.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(diagnostics) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.componentName, style = MaterialTheme.typography.titleMedium)
                            Text(item.details, style = MaterialTheme.typography.bodySmall)
                        }
                        val statusColor = when (item.status) {
                            DiagnosticStatus.OK -> Color(0xFF2E7D32)
                            DiagnosticStatus.WARNING -> Color(0xFFF9A825)
                            DiagnosticStatus.ERROR -> MaterialTheme.colorScheme.error
                        }
                        Text(item.status.name, color = statusColor, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
