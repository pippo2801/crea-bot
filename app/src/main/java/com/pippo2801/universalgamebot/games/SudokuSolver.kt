package com.pippo2801.universalgamebot.games

/**
 * Offline Sudoku solver for a 9x9 puzzle. Zero denotes an empty cell.
 * It validates givens before searching and counts solutions up to two so callers
 * can distinguish a unique solution from an ambiguous puzzle.
 */
object SudokuSolver {
    enum class Status { INVALID_INPUT, UNSOLVABLE, SOLVED, MULTIPLE_SOLUTIONS }

    data class Result(
        val status: Status,
        val grid: List<List<Int>>? = null,
        val solutionsFound: Int = 0
    )

    fun solve(input: List<List<Int>>): Result {
        if (input.size != 9 || input.any { it.size != 9 || it.any { value -> value !in 0..9 } }) {
            return Result(Status.INVALID_INPUT)
        }

        val board = Array(9) { row -> input[row].toIntArray() }
        if (!givensAreValid(board)) return Result(Status.INVALID_INPUT)

        var solutions = 0
        var firstSolution: Array<IntArray>? = null

        fun candidates(row: Int, col: Int): List<Int> {
            val used = BooleanArray(10)
            for (i in 0 until 9) {
                used[board[row][i]] = true
                used[board[i][col]] = true
            }
            val boxRow = (row / 3) * 3
            val boxCol = (col / 3) * 3
            for (r in boxRow until boxRow + 3) {
                for (c in boxCol until boxCol + 3) used[board[r][c]] = true
            }
            return (1..9).filterNot { used[it] }
        }

        fun search() {
            if (solutions >= 2) return
            var bestRow = -1
            var bestCol = -1
            var bestCandidates: List<Int> = emptyList()

            for (row in 0 until 9) {
                for (col in 0 until 9) {
                    if (board[row][col] != 0) continue
                    val options = candidates(row, col)
                    if (options.isEmpty()) return
                    if (bestRow == -1 || options.size < bestCandidates.size) {
                        bestRow = row
                        bestCol = col
                        bestCandidates = options
                        if (options.size == 1) break
                    }
                }
                if (bestRow >= 0 && bestCandidates.size == 1) break
            }

            if (bestRow == -1) {
                solutions++
                if (firstSolution == null) firstSolution = Array(9) { board[it].clone() }
                return
            }

            for (value in bestCandidates) {
                board[bestRow][bestCol] = value
                search()
                board[bestRow][bestCol] = 0
                if (solutions >= 2) return
            }
        }

        search()
        val status = when (solutions) {
            0 -> Status.UNSOLVABLE
            1 -> Status.SOLVED
            else -> Status.MULTIPLE_SOLUTIONS
        }
        val solved = firstSolution?.map { row -> row.toList() }
        return Result(status, solved, solutions)
    }

    private fun givensAreValid(board: Array<IntArray>): Boolean {
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val value = board[row][col]
                if (value == 0) continue
                board[row][col] = 0
                val duplicate = (0 until 9).any { board[row][it] == value || board[it][col] == value } ||
                    (row / 3 * 3 until row / 3 * 3 + 3).any { r ->
                        (col / 3 * 3 until col / 3 * 3 + 3).any { c -> board[r][c] == value }
                    }
                board[row][col] = value
                if (duplicate) return false
            }
        }
        return true
    }
}
