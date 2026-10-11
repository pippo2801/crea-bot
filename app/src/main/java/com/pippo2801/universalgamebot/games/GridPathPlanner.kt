package com.pippo2801.universalgamebot.games

/**
 * Combines the logical path solver and screen-coordinate mapper.
 * Input grid and board bounds must come from a separately validated recognizer.
 * The result is a plan only; it never dispatches gestures or marks a game certified.
 */
object GridPathPlanner {
    enum class Failure { INVALID_GRID, UNREACHABLE, INVALID_BOARD_BOUNDS }

    data class Plan(
        val path: List<GridPoint>,
        val screenPoints: List<ScreenPoint>
    )

    data class Result(
        val plan: Plan? = null,
        val failure: Failure? = null
    ) {
        val isSuccess: Boolean get() = plan != null && failure == null
    }

    fun plan(
        grid: List<String>,
        bounds: GridBounds,
        screenWidth: Int,
        screenHeight: Int
    ): Result {
        if (grid.isEmpty() || grid[0].isEmpty() ||
            grid.any { it.length != grid[0].length || it.any { cell -> cell !in "#.SG" } } ||
            grid.sumOf { line -> line.count { it == 'S' } } != 1 ||
            grid.sumOf { line -> line.count { it == 'G' } } != 1
        ) return Result(failure = Failure.INVALID_GRID)

        val path = GridPathSolver.findPath(grid)
            ?: return Result(failure = Failure.UNREACHABLE)

        val screenPoints = GridCoordinateMapper.pathCenters(
            path = path,
            rows = grid.size,
            columns = grid[0].length,
            bounds = bounds,
            screenWidth = screenWidth,
            screenHeight = screenHeight
        ) ?: return Result(failure = Failure.INVALID_BOARD_BOUNDS)

        return Result(plan = Plan(path, screenPoints))
    }
}
