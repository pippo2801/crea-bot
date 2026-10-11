package com.pippo2801.universalgamebot.games

/**
 * Parses manually entered Sudoku text without silently discarding invalid characters.
 * Empty cells may be represented by 0 or '.', and whitespace between cells is allowed.
 */
object SudokuGridParser {
    enum class Error { INVALID_ROW_COUNT, INVALID_ROW_LENGTH, INVALID_CHARACTER }

    data class Result(val grid: List<List<Int>>? = null, val error: Error? = null) {
        val isValid: Boolean get() = grid != null && error == null
    }

    fun parse(text: String): Result {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size != 9) return Result(error = Error.INVALID_ROW_COUNT)

        val rows = mutableListOf<List<Int>>()
        for (line in lines) {
            val compact = line.filterNot { it.isWhitespace() }
            if (compact.any { it != '.' && it !in '0'..'9' }) {
                return Result(error = Error.INVALID_CHARACTER)
            }
            if (compact.length != 9) return Result(error = Error.INVALID_ROW_LENGTH)
            rows += compact.map { if (it == '.' || it == '0') 0 else it.digitToInt() }
        }
        return Result(grid = rows)
    }
}
