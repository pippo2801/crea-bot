package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridPathSolverTest {
    @Test
    fun findsShortestPathAndIncludesEndpoints() {
        val path = GridPathSolver.findPath(
            listOf(
                "S..",
                "##.",
                "..G"
            )
        )
        assertEquals(
            listOf(
                GridPoint(0, 0), GridPoint(0, 1), GridPoint(0, 2),
                GridPoint(1, 2), GridPoint(2, 2)
            ),
            path
        )
    }

    @Test
    fun returnsNullWhenGoalIsBlockedOff() {
        assertNull(GridPathSolver.findPath(listOf("S#G", "###", "...")))
    }

    @Test
    fun rejectsMissingOrDuplicateEndpoints() {
        assertNull(GridPathSolver.findPath(listOf("...", "..G")))
        assertNull(GridPathSolver.findPath(listOf("SSG")))
        assertNull(GridPathSolver.findPath(listOf("SGG")))
    }

    @Test
    fun rejectsEmptyRaggedOrInvalidGrid() {
        assertNull(GridPathSolver.findPath(emptyList()))
        assertNull(GridPathSolver.findPath(listOf("S", "G.")))
        assertNull(GridPathSolver.findPath(listOf("SxG")))
    }

    @Test
    fun findsAdjacentGoal() {
        val path = GridPathSolver.findPath(listOf("SG"))
        assertEquals(2, path?.size)
        assertTrue(path!!.first() == GridPoint(0, 0))
        assertTrue(path.last() == GridPoint(0, 1))
    }
}
