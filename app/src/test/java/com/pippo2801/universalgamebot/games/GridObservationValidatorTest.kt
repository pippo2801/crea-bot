package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GridObservationValidatorTest {
    private fun candidate(
        rows: List<String> = listOf("S..", ".#.", "..G"),
        gameId: String = "amaze_go",
        bounds: GridBounds = GridBounds(10f, 20f, 310f, 320f),
        width: Int = 400,
        height: Int = 500,
        confidence: Float = 0.98f
    ) = GridObservation(gameId, rows, bounds, width, height, confidence)

    @Test fun acceptsWellFormedHighConfidenceObservation() {
        val result = GridObservationValidator.validate(candidate())
        assertTrue(result.isValid)
        assertEquals(candidate(), result.observation)
    }

    @Test fun rejectsUnknownGameAndLowConfidence() {
        assertEquals(
            GridObservationValidator.Failure.UNKNOWN_GAME,
            GridObservationValidator.validate(candidate(gameId = "unknown")).failure
        )
        assertEquals(
            GridObservationValidator.Failure.LOW_CONFIDENCE,
            GridObservationValidator.validate(candidate(confidence = 0.949f)).failure
        )
    }

    @Test fun rejectsMalformedGridAndDuplicateMarkers() {
        assertEquals(
            GridObservationValidator.Failure.INVALID_GRID,
            GridObservationValidator.validate(candidate(rows = listOf("SS.", "...", "..G"))).failure
        )
        assertEquals(
            GridObservationValidator.Failure.INVALID_DIMENSIONS,
            GridObservationValidator.validate(candidate(rows = listOf("S..", "..G", "."))).failure
        )
        assertFalse(GridObservationValidator.validate(candidate(rows = listOf("Sx.", "...", "..G"))).isValid)
    }

    @Test fun rejectsInvalidConfidenceAndScreenBounds() {
        assertEquals(
            GridObservationValidator.Failure.INVALID_CONFIDENCE,
            GridObservationValidator.validate(candidate(confidence = Float.NaN)).failure
        )
        assertEquals(
            GridObservationValidator.Failure.INVALID_BOUNDS,
            GridObservationValidator.validate(candidate(bounds = GridBounds(-1f, 0f, 10f, 10f))).failure
        )
        assertEquals(
            GridObservationValidator.Failure.INVALID_SCREEN_SIZE,
            GridObservationValidator.validate(candidate(width = 0)).failure
        )
    }

    @Test fun rejectsGridWithoutExactlyOneStartAndGoal() {
        assertEquals(
            GridObservationValidator.Failure.INVALID_GRID,
            GridObservationValidator.validate(candidate(rows = listOf("...", ".#.", "..."))).failure
        )
    }
}
