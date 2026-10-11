package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuSolverTest {
    private val puzzle = listOf(
        listOf(5,3,0,0,7,0,0,0,0),
        listOf(6,0,0,1,9,5,0,0,0),
        listOf(0,9,8,0,0,0,0,6,0),
        listOf(8,0,0,0,6,0,0,0,3),
        listOf(4,0,0,8,0,3,0,0,1),
        listOf(7,0,0,0,2,0,0,0,6),
        listOf(0,6,0,0,0,0,2,8,0),
        listOf(0,0,0,4,1,9,0,0,5),
        listOf(0,0,0,0,8,0,0,7,9)
    )

    @Test
    fun solvesStandardPuzzleUniquely() {
        val result = SudokuSolver.solve(puzzle)
        assertEquals(SudokuSolver.Status.SOLVED, result.status)
        assertEquals(1, result.solutionsFound)
        val grid = result.grid
        assertNotNull(grid)
        assertTrue(grid!!.all { row -> row.size == 9 && row.all { it in 1..9 } })
        for (i in 0 until 9) {
            assertEquals((1..9).toSet(), grid[i].toSet())
            assertEquals((1..9).toSet(), (0 until 9).map { grid[it][i] }.toSet())
        }
    }

    @Test
    fun rejectsWrongDimensionsAndValues() {
        assertEquals(SudokuSolver.Status.INVALID_INPUT, SudokuSolver.solve(emptyList()).status)
        assertEquals(
            SudokuSolver.Status.INVALID_INPUT,
            SudokuSolver.solve(List(9) { List(8) { 0 } }).status
        )
        val invalidValue = puzzle.map { it.toMutableList() }.map { it.toList() }.toMutableList()
        invalidValue[0] = invalidValue[0].toMutableList().also { it[0] = 10 }
        assertEquals(SudokuSolver.Status.INVALID_INPUT, SudokuSolver.solve(invalidValue).status)
    }

    @Test
    fun rejectsDuplicateGivens() {
        val invalid = puzzle.map { it.toMutableList() }.toMutableList()
        invalid[0][1] = 5
        assertEquals(SudokuSolver.Status.INVALID_INPUT, SudokuSolver.solve(invalid).status)
    }

    @Test
    fun identifiesPuzzleWithMultipleSolutions() {
        val result = SudokuSolver.solve(List(9) { List(9) { 0 } })
        assertEquals(SudokuSolver.Status.MULTIPLE_SOLUTIONS, result.status)
        assertEquals(2, result.solutionsFound)
        assertNotNull(result.grid)
    }

    @Test
    fun doesNotMutateInputPuzzle() {
        val before = puzzle.map { it.toList() }
        SudokuSolver.solve(puzzle)
        assertEquals(before, puzzle)
    }
}
