package com.pippo2801.universalgamebot.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pippo2801.universalgamebot.games.SudokuSolver
import com.pippo2801.universalgamebot.games.SudokuGridParser

private val defaultSudoku = listOf(
    "530070000",
    "600195000",
    "098000060",
    "800060003",
    "400803001",
    "700020006",
    "060000280",
    "000419005",
    "000080079"
).joinToString("\n")

@Composable
fun SudokuToolScreen() {
    var puzzleText by remember { mutableStateOf(defaultSudoku) }
    var resultText by remember { mutableStateOf("Inserisci una griglia 9×9: usa 0 o . per le caselle vuote.") }
    var solvedGrid by remember { mutableStateOf<List<List<Int>>?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Risolutore Sudoku offline", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Funziona senza internet. Ogni riga deve contenere 9 cifre da 1 a 9; usa 0 o . per una casella vuota. La griglia di esempio è modificabile.",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = puzzleText,
            onValueChange = { puzzleText = it; solvedGrid = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Griglia Sudoku (9 righe)") },
            minLines = 9,
            maxLines = 12,
            supportingText = { Text("Sono accettati spazi, punti e zeri; le cifre non vuote devono essere 1–9.") }
        )
        Button(
            onClick = {
                val parsed = SudokuGridParser.parse(puzzleText)
                if (!parsed.isValid) {
                    solvedGrid = null
                    resultText = when (parsed.error) {
                        SudokuGridParser.Error.INVALID_ROW_COUNT ->
                            "Formato non valido: servono esattamente 9 righe non vuote."
                        SudokuGridParser.Error.INVALID_ROW_LENGTH ->
                            "Formato non valido: ogni riga deve contenere esattamente 9 celle."
                        SudokuGridParser.Error.INVALID_CHARACTER ->
                            "Carattere non valido: usa solo cifre da 0 a 9, punti e spazi."
                        null -> "Formato non valido."
                    }
                } else {
                    val result = SudokuSolver.solve(parsed.grid!!)
                    solvedGrid = if (result.status == SudokuSolver.Status.SOLVED) result.grid else null
                    resultText = when (result.status) {
                        SudokuSolver.Status.INVALID_INPUT -> "Griglia non valida: controlla cifre duplicate nelle righe, colonne o riquadri."
                        SudokuSolver.Status.UNSOLVABLE -> "Nessuna soluzione: i numeri inseriti sono incompatibili."
                        SudokuSolver.Status.SOLVED -> "Soluzione unica trovata."
                        SudokuSolver.Status.MULTIPLE_SOLUTIONS -> "La griglia ammette più soluzioni; controlla gli indizi."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Risolvi Sudoku") }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(resultText, style = MaterialTheme.typography.bodyMedium)
                solvedGrid?.let { grid ->
                    Spacer(modifier = Modifier.height(12.dp))
                    grid.forEachIndexed { index, row ->
                        Text(row.joinToString(" "), style = MaterialTheme.typography.bodyLarge)
                        if (index == 2 || index == 5) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { puzzleText = grid.joinToString("\n") { it.joinToString("") }; resultText = "Soluzione copiata nell'area di modifica."; solvedGrid = null }) {
                            Text("Usa soluzione come griglia")
                        }
                    }
                }
            }
        }
    }
}
