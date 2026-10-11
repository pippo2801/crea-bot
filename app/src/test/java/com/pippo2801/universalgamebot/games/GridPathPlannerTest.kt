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
        assertEquals(50f, plan.screenPoints.first().x, 0.001f)
        assertEquals(33.3333f, plan.screenPoints.first().y, 0.01f)
        assertEquals(250f, plan.screenPoints.last().x, 0.001f)
        assertEquals(166.6667f, plan.screenPoints.last().y, 0.01f)
    }

    @Test
    fun distinguishesInvalidGridFromUnreachableGoal() {
        val invalid = GridPathPlanner.plan(listOf("S##", "###", "..G"), bounds, 300, 200)
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
    @Test
    fun validatedObservationCanBePlannedButLowConfidenceIsRejected() {
        val observation = GridObservation(
            gameId = "amaze_go",
            rows = listOf("S..", "##.", "..G"),
            boardBounds = bounds,
            screenWidth = 300,
            screenHeight = 200,
            confidence = 0.98f
        )
        assertTrue(GridPathPlanner.planValidated(observation).isSuccess)

        val uncertain = observation.copy(confidence = 0.5f)
        assertEquals(
            GridPathPlanner.Failure.INVALID_OBSERVATION,
            GridPathPlanner.planValidated(uncertain).failure
        )
    }

}
