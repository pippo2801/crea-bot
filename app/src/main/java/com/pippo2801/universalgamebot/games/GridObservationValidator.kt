package com.pippo2801.universalgamebot.games

/**
 * A grid observation is data emitted by a future, game-specific visual recognizer.
 * Constructing this object does not imply that the pixels were actually recognized:
 * callers must supply measured confidence and screen-space board bounds.
 */
data class GridObservation(
    val gameId: String,
    val rows: List<String>,
    val boardBounds: GridBounds,
    val screenWidth: Int,
    val screenHeight: Int,
    val confidence: Float
)

object GridObservationValidator {
    const val MIN_CONFIDENCE = 0.95f
    const val MAX_ROWS = 40
    const val MAX_COLUMNS = 40

    enum class Failure {
        UNKNOWN_GAME,
        INVALID_DIMENSIONS,
        INVALID_GRID,
        INVALID_SCREEN_SIZE,
        INVALID_CONFIDENCE,
        LOW_CONFIDENCE,
        INVALID_BOUNDS
    }

    data class Result(val observation: GridObservation? = null, val failure: Failure? = null) {
        val isValid: Boolean get() = observation != null && failure == null
    }

    fun validate(candidate: GridObservation): Result {
        if (candidate.gameId.isBlank() || candidate.gameId == "unknown") {
            return Result(failure = Failure.UNKNOWN_GAME)
        }
        if (candidate.rows.isEmpty() || candidate.rows.size > MAX_ROWS ||
            candidate.rows[0].isEmpty() || candidate.rows[0].length > MAX_COLUMNS ||
            candidate.rows.any { it.length != candidate.rows[0].length }
        ) return Result(failure = Failure.INVALID_DIMENSIONS)

        if (candidate.rows.any { row -> row.any { it !in "#.SG" } } ||
            candidate.rows.sumOf { row -> row.count { it == 'S' } } != 1 ||
            candidate.rows.sumOf { row -> row.count { it == 'G' } } != 1
        ) return Result(failure = Failure.INVALID_GRID)

        if (candidate.screenWidth <= 0 || candidate.screenHeight <= 0) {
            return Result(failure = Failure.INVALID_SCREEN_SIZE)
        }
        if (!candidate.confidence.isFinite() || candidate.confidence !in 0f..1f) {
            return Result(failure = Failure.INVALID_CONFIDENCE)
        }
        if (candidate.confidence < MIN_CONFIDENCE) {
            return Result(failure = Failure.LOW_CONFIDENCE)
        }

        val bounds = candidate.boardBounds
        if (!bounds.left.isFinite() || !bounds.top.isFinite() ||
            !bounds.right.isFinite() || !bounds.bottom.isFinite() ||
            bounds.left < 0f || bounds.top < 0f ||
            bounds.right <= bounds.left || bounds.bottom <= bounds.top ||
            bounds.right > candidate.screenWidth.toFloat() ||
            bounds.bottom > candidate.screenHeight.toFloat()
        ) return Result(failure = Failure.INVALID_BOUNDS)

        return Result(observation = candidate)
    }
}
