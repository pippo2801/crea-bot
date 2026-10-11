package com.pippo2801.universalgamebot.games

/**
 * Converts logical grid cells to screen-space cell centers.
 * The caller must supply a board rectangle measured from a trusted recognizer;
 * this mapper does not infer the board from pixels and cannot certify a game.
 */
data class ScreenPoint(val x: Float, val y: Float)

data class GridBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

object GridCoordinateMapper {
    fun cellCenter(
        point: GridPoint,
        rows: Int,
        columns: Int,
        bounds: GridBounds,
        screenWidth: Int,
        screenHeight: Int
    ): ScreenPoint? {
        if (rows <= 0 || columns <= 0 || screenWidth <= 0 || screenHeight <= 0) return null
        if (point.row !in 0 until rows || point.col !in 0 until columns) return null
        val values = listOf(bounds.left, bounds.top, bounds.right, bounds.bottom)
        if (values.any { !it.isFinite() }) return null
        if (bounds.left < 0f || bounds.top < 0f ||
            bounds.right <= bounds.left || bounds.bottom <= bounds.top ||
            bounds.right > screenWidth.toFloat() || bounds.bottom > screenHeight.toFloat()
        ) return null

        val cellWidth = (bounds.right - bounds.left) / columns
        val cellHeight = (bounds.bottom - bounds.top) / rows
        val x = bounds.left + (point.col + 0.5f) * cellWidth
        val y = bounds.top + (point.row + 0.5f) * cellHeight
        if (!x.isFinite() || !y.isFinite() || x !in 0f..screenWidth.toFloat() ||
            y !in 0f..screenHeight.toFloat()
        ) return null
        return ScreenPoint(x, y)
    }

    fun pathCenters(
        path: List<GridPoint>,
        rows: Int,
        columns: Int,
        bounds: GridBounds,
        screenWidth: Int,
        screenHeight: Int
    ): List<ScreenPoint>? {
        if (path.isEmpty()) return null
        val mapped = path.map { point ->
            cellCenter(point, rows, columns, bounds, screenWidth, screenHeight) ?: return null
        }
        return mapped
    }
}
