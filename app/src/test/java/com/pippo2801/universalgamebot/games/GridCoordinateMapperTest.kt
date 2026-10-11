package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GridCoordinateMapperTest {
    private val bounds = GridBounds(100f, 200f, 500f, 800f)

    @Test
    fun mapsCellToItsCenter() {
        assertEquals(
            ScreenPoint(150f, 250f),
            GridCoordinateMapper.cellCenter(GridPoint(0, 0), 6, 4, bounds, 600, 900)
        )
        assertEquals(
            ScreenPoint(450f, 750f),
            GridCoordinateMapper.cellCenter(GridPoint(5, 3), 6, 4, bounds, 600, 900)
        )
    }

    @Test
    fun rejectsOutOfRangeCellAndInvalidGridDimensions() {
        assertNull(GridCoordinateMapper.cellCenter(GridPoint(6, 0), 6, 4, bounds, 600, 900))
        assertNull(GridCoordinateMapper.cellCenter(GridPoint(0, 0), 0, 4, bounds, 600, 900))
        assertNull(GridCoordinateMapper.cellCenter(GridPoint(0, 0), 6, 4, bounds, 0, 900))
    }

    @Test
    fun rejectsInvalidOrOutOfScreenBounds() {
        assertNull(
            GridCoordinateMapper.cellCenter(
                GridPoint(0, 0), 6, 4, GridBounds(-1f, 0f, 10f, 10f), 600, 900
            )
        )
        assertNull(
            GridCoordinateMapper.cellCenter(
                GridPoint(0, 0), 6, 4, GridBounds(10f, 10f, 9f, 20f), 600, 900
            )
        )
        assertNull(
            GridCoordinateMapper.cellCenter(
                GridPoint(0, 0), 6, 4, GridBounds(10f, 10f, 700f, 20f), 600, 900
            )
        )
    }

    @Test
    fun rejectsNonFiniteCoordinatesAndEmptyPath() {
        assertNull(
            GridCoordinateMapper.cellCenter(
                GridPoint(0, 0), 6, 4,
                GridBounds(Float.NaN, 10f, 100f, 100f), 600, 900
            )
        )
        assertNull(GridCoordinateMapper.pathCenters(emptyList(), 6, 4, bounds, 600, 900))
    }

    @Test
    fun mapsWholePathAndRejectsPathWithInvalidPoint() {
        val path = listOf(GridPoint(0, 0), GridPoint(0, 1), GridPoint(1, 1))
        assertEquals(
            listOf(ScreenPoint(150f, 250f), ScreenPoint(250f, 250f), ScreenPoint(250f, 350f)),
            GridCoordinateMapper.pathCenters(path, 6, 4, bounds, 600, 900)
        )
        assertNull(
            GridCoordinateMapper.pathCenters(
                listOf(GridPoint(0, 0), GridPoint(99, 1)), 6, 4, bounds, 600, 900
            )
        )
    }
}
