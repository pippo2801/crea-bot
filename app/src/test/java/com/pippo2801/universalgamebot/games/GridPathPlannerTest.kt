package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridPathPlannerTest {
    private val bounds = GridBounds(0f, 0f, 300f, 200f)

    @Test
    fun producesShortestPathAndScreenCenters() {
        val result = GridPathPlanner.plan(
            listOf("S..", "##.", "..G"), bounds, 300, 200
        )
        assertTrue(result.isSuccess)
        assertNull(result.failure)
        val plan = result.plan
        assertNotNull(plan)
        assertEquals(5, plan!!.path.size)
        assertEquals(plan.path.size, plan.screenPoints.size)
        assertEquals(ScreenPoint(50f, 33.333336f), plan.screenPoints.first())
        assertEquals(ScreenPoint(250f, 166.66667f), plan.screenPoints.last())
    }

    @Test
    fun distinguishesInvalidGridFromUnreachableGoal() {
        val invalid = GridPathPlanner.plan(listOf("S..", "..G", "###"), bounds, 300, 200)
        assertEquals(GridPathPlanner.Failure.UNREACHABLE, invalid.failure)
        assertFalse(invalid.isSuccess)

        val malformed = GridPathPlanner.plan(listOf("S..", "..."), bounds, 300, 200)
        assertEquals(GridPathPlanner.Failure.INVALID_GRID, malformed.failure)
    }

    @Test
    fun rejectsInvalidBoardBounds() {
        val result = GridPathPlanner.plan(
            listOf("SG"), GridBounds(-1f, 0f, 300f, 200f), 300, 200
        )
        assertEquals(GridPathPlanner.Failure.INVALID_BOARD_BOUNDS, result.failure)
        assertNull(result.plan)
    }
}
