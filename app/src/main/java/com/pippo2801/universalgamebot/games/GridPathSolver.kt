package com.pippo2801.universalgamebot.games

import java.util.ArrayDeque

/**
 * Deterministic shortest-path solver for a grid already recognized by a trusted vision layer.
 *
 * Symbols: '#' = blocked, '.' = walkable, 'S' = start, 'G' = goal.
 * Returns a path including both endpoints, or null for malformed/unreachable grids.
 * This class does not recognize pixels and must not be used to authorize gameplay by itself.
 */
data class GridPoint(val row: Int, val col: Int)

object GridPathSolver {
    private val directions = listOf(
        GridPoint(-1, 0), // up
        GridPoint(0, -1), // left
        GridPoint(0, 1),  // right
        GridPoint(1, 0)   // down
    )

    fun findPath(grid: List<String>): List<GridPoint>? {
        if (grid.isEmpty() || grid[0].isEmpty()) return null
        val width = grid[0].length
        if (grid.any { it.length != width || it.any { cell -> cell !in "#.SG" } }) return null

        val starts = mutableListOf<GridPoint>()
        val goals = mutableListOf<GridPoint>()
        grid.forEachIndexed { row, line ->
            line.forEachIndexed { col, cell ->
                when (cell) {
                    'S' -> starts += GridPoint(row, col)
                    'G' -> goals += GridPoint(row, col)
                }
            }
        }
        if (starts.size != 1 || goals.size != 1) return null

        val start = starts.single()
        val goal = goals.single()
        val queue = ArrayDeque<GridPoint>()
        val previous = mutableMapOf<GridPoint, GridPoint?>()
        queue.addLast(start)
        previous[start] = null

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == goal) {
                val path = mutableListOf<GridPoint>()
                var cursor: GridPoint? = goal
                while (cursor != null) {
                    path += cursor
                    cursor = previous[cursor]
                }
                path.reverse()
                return path
            }

            for (direction in directions) {
                val next = GridPoint(current.row + direction.row, current.col + direction.col)
                if (next.row !in grid.indices || next.col !in 0 until width) continue
                if (grid[next.row][next.col] == '#') continue
                if (previous.containsKey(next)) continue
                previous[next] = current
                queue.addLast(next)
            }
        }
        return null
    }
}
