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
    // For demo/diagnostics, assuming accessibility not bound yet
    val diagnostics = viewModel.diagnosticsManager.runDiagnostics(false, false)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Diagnostics & Laboratory", style = MaterialTheme.typography.headlineMedium)
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
                            DiagnosticStatus.OK -> Color.Green
                            DiagnosticStatus.WARNING -> Color.Yellow
                            DiagnosticStatus.ERROR -> Color.Red
                        }
                        Text(item.status.name, color = statusColor, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
