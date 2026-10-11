package com.pippo2801.universalgamebot.vision

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridImageRecognizerTest {

    private val recognizer = GridImageRecognizer(
        AmazeGoVisualConfig(
            wallColorRange = ColorRange(0, 30, 0, 30, 0, 30),
            freeColorRange = ColorRange(230, 255, 230, 255, 230, 255),
            startColorRange = ColorRange(0, 50, 200, 255, 0, 50),
            goalColorRange = ColorRange(200, 255, 0, 50, 0, 50)
        )
    )

    @Test
    fun syntheticGridWithUniqueStartAndGoalIsRecognized() {
        val result = recognizer.recognize(syntheticGrid())

        assertTrue("Expected success but got $result", result is GridRecognitionResult.Success)
        val grid = (result as GridRecognitionResult.Success).grid
        assertEquals(5, grid.rows.size)
        assertTrue(grid.rows.all { it.length == 5 })
        assertEquals(1, grid.rows.sumOf { it.count { cell -> cell == 'S' } })
        assertEquals(1, grid.rows.sumOf { it.count { cell -> cell == 'G' } })
        assertTrue(grid.confidence >= 0.95f)
        assertEquals("amaze_go", grid.observation.gameId)
    }

    @Test
    fun plainImageFailsClosedWithoutInventingAGrid() {
        val pixels = IntArray(160 * 160) { WHITE }
        val result = recognizer.recognize(PixelFrame(160, 160, pixels))

        assertTrue(result is GridRecognitionResult.Failure)
        assertEquals(
            GridRecognitionError.GRID_NOT_FOUND,
            (result as GridRecognitionResult.Failure).reason
        )
    }

    @Test
    fun unclassifiedCellBlocksRecognition() {
        val frame = syntheticGrid(ambiguousCell = Pair(2, 2))
        val result = recognizer.recognize(frame)

        assertTrue(result is GridRecognitionResult.Failure)
        assertEquals(
            GridRecognitionError.AMBIGUOUS_CELLS,
            (result as GridRecognitionResult.Failure).reason
        )
    }

    @Test
    fun unsupportedResolutionIsRejected() {
        val result = recognizer.recognize(PixelFrame(40, 40, IntArray(40 * 40) { WHITE }))

        assertTrue(result is GridRecognitionResult.Failure)
        assertEquals(
            GridRecognitionError.UNSUPPORTED_RESOLUTION,
            (result as GridRecognitionResult.Failure).reason
        )
    }

    private fun syntheticGrid(ambiguousCell: Pair<Int, Int>? = null): PixelFrame {
        val width = 160
        val height = 160
        val pixels = IntArray(width * height) { 0xFF777777.toInt() }
        val origin = 12
        val cellSize = 24
        val cells = Array(5) { Array(5) { FREE } }
        cells[1][1] = WALL
        cells[0][0] = START
        cells[4][4] = GOAL
        if (ambiguousCell != null) cells[ambiguousCell.first][ambiguousCell.second] = AMBIGUOUS

        for (row in 0 until 5) {
            for (column in 0 until 5) {
                val left = origin + column * cellSize
                val top = origin + row * cellSize
                for (y in top + 2 until top + cellSize - 1) {
                    for (x in left + 2 until left + cellSize - 1) {
                        pixels[y * width + x] = cells[row][column]
                    }
                }
            }
        }

        // Draw two-pixel grid lines over the fills.
        for (line in 0..5) {
            val position = origin + line * cellSize
            for (offset in 0..1) {
                val x = position + offset
                if (x in 0 until width) {
                    for (y in origin until origin + 5 * cellSize) {
                        pixels[y * width + x] = WALL
                    }
                }
                val y = position + offset
                if (y in 0 until height) {
                    for (x in origin until origin + 5 * cellSize) {
                        pixels[y * width + x] = WALL
                    }
                }
            }
        }
        return PixelFrame(width, height, pixels)
    }

    private companion object {
        const val WALL = 0xFF000000.toInt()
        const val FREE = 0xFFFFFFFF.toInt()
        const val START = 0xFF00FF00.toInt()
        const val GOAL = 0xFFFF0000.toInt()
        const val AMBIGUOUS = 0xFF880088.toInt()
        const val WHITE = 0xFFFFFFFF.toInt()
    }
}
