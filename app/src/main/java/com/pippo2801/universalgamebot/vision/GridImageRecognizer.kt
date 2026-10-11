package com.pippo2801.universalgamebot.vision

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.games.GridBounds
import com.pippo2801.universalgamebot.games.GridObservation

/**
 * Immutable ARGB pixel buffer. Kept separate from Bitmap so recognition can be tested on the JVM
 * without Android graphics mocks or a physical device.
 */
data class PixelFrame(
    val width: Int,
    val height: Int,
    val pixels: IntArray
) {
    init {
        require(width > 0 && height > 0) { "Image dimensions must be positive" }
        require(pixels.size == width * height) { "Pixel count does not match image dimensions" }
    }

    fun pixel(x: Int, y: Int): Int = pixels[y * width + x]

    companion object {
        fun fromBitmap(bitmap: Bitmap): PixelFrame {
            require(bitmap.width > 0 && bitmap.height > 0) { "Bitmap dimensions must be positive" }
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            return PixelFrame(bitmap.width, bitmap.height, pixels)
        }
    }
}

data class ColorRange(
    val minRed: Int, val maxRed: Int,
    val minGreen: Int, val maxGreen: Int,
    val minBlue: Int, val maxBlue: Int
) {
    init {
        require(listOf(minRed, maxRed, minGreen, maxGreen, minBlue, maxBlue).all { it in 0..255 })
        require(minRed <= maxRed && minGreen <= maxGreen && minBlue <= maxBlue)
    }

    fun contains(argb: Int): Boolean =
        ((argb ushr 16) and 0xFF) in minRed..maxRed &&
            ((argb ushr 8) and 0xFF) in minGreen..maxGreen &&
            (argb and 0xFF) in minBlue..maxBlue
}

data class AmazeGoVisualConfig(
    val wallColorRange: ColorRange,
    val freeColorRange: ColorRange,
    val startColorRange: ColorRange,
    val goalColorRange: ColorRange,
    val minWidth: Int = 120,
    val minHeight: Int = 120,
    val minCellSizePx: Int = 8,
    val maxCellSizePx: Int = 240,
    val maxRows: Int = 40,
    val maxColumns: Int = 40,
    val edgePixelThreshold: Int = 45,
    val minLineCoverage: Float = 0.35f,
    val lineMergeDistancePx: Int = 3,
    val minCellColorMatchRatio: Float = 0.85f,
) {
    init {
        require(minWidth > 0 && minHeight > 0)
        require(minCellSizePx >= 2 && maxCellSizePx >= minCellSizePx)
        require(maxRows >= 2 && maxColumns >= 2)
        require(edgePixelThreshold in 1..255)
        require(minLineCoverage in 0f..1f)
        require(lineMergeDistancePx >= 0)
        require(minCellColorMatchRatio in 0f..1f)
    }
}

enum class GridCellType(val symbol: Char) {
    WALL('#'),
    FREE('.'),
    START('S'),
    GOAL('G')
}

data class GridRegion(val left: Int, val top: Int, val right: Int, val bottom: Int)

data class RecognizedGrid(
    val rows: List<String>,
    val region: GridRegion,
    val confidence: Float,
    val observation: GridObservation
)

enum class GridRecognitionError {
    UNSUPPORTED_RESOLUTION,
    GRID_NOT_FOUND,
    PARTIAL_GRID,
    INVALID_GRID_DIMENSIONS,
    AMBIGUOUS_CELLS,
    START_OR_GOAL_NOT_UNIQUE
}

sealed class GridRecognitionResult {
    data class Success(val grid: RecognizedGrid) : GridRecognitionResult()
    data class Failure(val reason: GridRecognitionError, val details: String) : GridRecognitionResult()
}

/**
 * Conservative, configurable pixel-based grid recognizer.
 *
 * It detects repeated high-contrast horizontal/vertical lines and classifies cell interiors
 * using explicit color ranges. The ranges are game/theme specific and must be calibrated against
 * real Amaze GO screenshots before this recognizer can be considered production-ready.
 *
 * It deliberately does not execute gestures, authorize autonomy, or certify a game.
 */
class GridImageRecognizer(private val config: AmazeGoVisualConfig) {

    fun recognize(bitmap: Bitmap?): GridRecognitionResult {
        if (bitmap == null) {
            return failure(GridRecognitionError.UNSUPPORTED_RESOLUTION, "Bitmap is null")
        }
        return try {
            recognize(PixelFrame.fromBitmap(bitmap))
        } catch (exception: IllegalArgumentException) {
            failure(
                GridRecognitionError.UNSUPPORTED_RESOLUTION,
                exception.message ?: "Invalid bitmap"
            )
        }
    }

    fun recognize(frame: PixelFrame): GridRecognitionResult {
        if (frame.width < config.minWidth || frame.height < config.minHeight) {
            return failure(
                GridRecognitionError.UNSUPPORTED_RESOLUTION,
                "Image ${frame.width}x${frame.height} is smaller than ${config.minWidth}x${config.minHeight}"
            )
        }

        val verticalLines = detectLines(frame, vertical = true)
        val horizontalLines = detectLines(frame, vertical = false)
        val xSequence = findRegularSequence(verticalLines, frame.width, config.maxColumns)
        val ySequence = findRegularSequence(horizontalLines, frame.height, config.maxRows)

        if (xSequence.size < 3 || ySequence.size < 3) {
            return failure(
                GridRecognitionError.GRID_NOT_FOUND,
                "Could not find at least three regular vertical and horizontal grid lines"
            )
        }

        val region = GridRegion(
            left = xSequence.first(),
            top = ySequence.first(),
            right = xSequence.last(),
            bottom = ySequence.last()
        )
        val edgeMargin = config.lineMergeDistancePx + 1
        if (region.left <= edgeMargin || region.top <= edgeMargin ||
            region.right >= frame.width - edgeMargin - 1 ||
            region.bottom >= frame.height - edgeMargin - 1
        ) {
            return failure(
                GridRecognitionError.PARTIAL_GRID,
                "Detected grid touches the image edge; a complete board cannot be confirmed"
            )
        }

        val columnWidths = xSequence.zipWithNext { a, b -> b - a }
        val rowHeights = ySequence.zipWithNext { a, b -> b - a }
        if (columnWidths.any { it !in config.minCellSizePx..config.maxCellSizePx } ||
            rowHeights.any { it !in config.minCellSizePx..config.maxCellSizePx } ||
            !isRegular(columnWidths) || !isRegular(rowHeights)
        ) {
            return failure(
                GridRecognitionError.INVALID_GRID_DIMENSIONS,
                "Detected line spacing is too small, too large, or inconsistent"
            )
        }

        val classifiedRows = mutableListOf<String>()
        var ambiguousCells = 0
        var accumulatedCellConfidence = 0.0
        for (row in 0 until ySequence.lastIndex) {
            val symbols = StringBuilder()
            for (column in 0 until xSequence.lastIndex) {
                val classification = classifyCell(
                    frame,
                    xSequence[column], ySequence[row],
                    xSequence[column + 1], ySequence[row + 1]
                )
                if (classification == null) {
                    ambiguousCells++
                    symbols.append('?')
                } else {
                    symbols.append(classification.first.symbol)
                    accumulatedCellConfidence += classification.second
                }
            }
            classifiedRows += symbols.toString()
        }

        val cellCount = (xSequence.size - 1) * (ySequence.size - 1)
        if (ambiguousCells > 0) {
            return failure(
                GridRecognitionError.AMBIGUOUS_CELLS,
                "Unable to classify $ambiguousCells of $cellCount cell interiors using configured color ranges"
            )
        }

        val rows = classifiedRows
        val starts = rows.sumOf { line -> line.count { it == 'S' } }
        val goals = rows.sumOf { line -> line.count { it == 'G' } }
        if (starts != 1 || goals != 1) {
            return failure(
                GridRecognitionError.START_OR_GOAL_NOT_UNIQUE,
                "Expected exactly one start and one goal; found start=$starts goal=$goals"
            )
        }

        val cellConfidence = (accumulatedCellConfidence / cellCount.toDouble()).toFloat()
        val lineConfidence = minOf(spacingConfidence(columnWidths), spacingConfidence(rowHeights))
        val confidence = (cellConfidence * lineConfidence).coerceIn(0f, 1f)
        val observation = GridObservation(
            gameId = "amaze_go",
            rows = rows,
            boardBounds = GridBounds(
                left = region.left.toFloat(),
                top = region.top.toFloat(),
                right = region.right.toFloat(),
                bottom = region.bottom.toFloat()
            ),
            screenWidth = frame.width,
            screenHeight = frame.height,
            confidence = confidence
        )

        return GridRecognitionResult.Success(
            RecognizedGrid(rows, region, confidence, observation)
        )
    }

    private fun detectLines(frame: PixelFrame, vertical: Boolean): List<Int> {
        val axisSize = if (vertical) frame.width else frame.height
        val orthogonalSize = if (vertical) frame.height else frame.width
        val scores = FloatArray(axisSize)

        for (position in 1 until axisSize - 1) {
            var edges = 0
            for (orthogonal in 1 until orthogonalSize - 1) {
                val before = if (vertical) frame.pixel(position - 1, orthogonal)
                    else frame.pixel(orthogonal, position - 1)
                val after = if (vertical) frame.pixel(position, orthogonal)
                    else frame.pixel(orthogonal, position)
                if (kotlin.math.abs(luminance(before) - luminance(after)) >= config.edgePixelThreshold) {
                    edges++
                }
            }
            scores[position] = edges.toFloat() / (orthogonalSize - 2).coerceAtLeast(1).toFloat()
        }

        val candidates = (1 until axisSize - 1)
            .filter { scores[it] >= config.minLineCoverage }
        if (candidates.isEmpty()) return emptyList()

        val groups = mutableListOf<MutableList<Int>>()
        candidates.forEach { position ->
            val current = groups.lastOrNull()
            if (current == null || position - current.last() > config.lineMergeDistancePx) {
                groups += mutableListOf(position)
            } else {
                current += position
            }
        }
        return groups.map { group ->
            val weightTotal = group.sumOf { scores[it].toDouble() }.coerceAtLeast(0.0001)
            (group.sumOf { it * scores[it].toDouble() } / weightTotal).toInt()
        }.distinct().sorted()
    }

    private fun findRegularSequence(lines: List<Int>, axisSize: Int, maxCells: Int): List<Int> {
        if (lines.size < 3) return emptyList()
        var best = emptyList<Int>()
        var bestResidual = Double.POSITIVE_INFINITY

        for (startIndex in lines.indices) {
            for (nextIndex in startIndex + 1 until lines.size) {
                val distance = lines[nextIndex] - lines[startIndex]
                val maxDivisions = minOf(maxCells, distance / config.minCellSizePx)
                for (cellsBetween in 1..maxDivisions) {
                    val step = distance.toFloat() / cellsBetween.toFloat()
                    if (step < config.minCellSizePx || step > config.maxCellSizePx) continue
                    val tolerance = maxOf(2, (step * 0.18f).toInt())
                    val candidate = mutableListOf<Int>()
                    var residual = 0.0
                    var cellIndex = 0
                    while (candidate.size < maxCells + 1) {
                        val expected = (lines[startIndex] + cellIndex * step).toInt()
                        if (expected !in 1 until axisSize - 1) break
                        val nearest = lines.minByOrNull { kotlin.math.abs(it - expected) }
                        if (nearest == null || kotlin.math.abs(nearest - expected) > tolerance) break
                        if (candidate.lastOrNull() != nearest) {
                            candidate += nearest
                            residual += kotlin.math.abs(nearest - expected).toDouble()
                        } else {
                            break
                        }
                        cellIndex++
                    }
                    if (candidate.size >= 3 &&
                        (candidate.size > best.size ||
                            (candidate.size == best.size && residual < bestResidual))
                    ) {
                        best = candidate
                        bestResidual = residual
                    }
                }
            }
        }
        return best
    }

    private fun classifyCell(
        frame: PixelFrame,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ): Pair<GridCellType, Double>? {
        val cellWidth = right - left
        val cellHeight = bottom - top
        if (cellWidth < 4 || cellHeight < 4) return null

        val sampleLeft = left + (cellWidth * 0.30f).toInt()
        val sampleRight = right - (cellWidth * 0.30f).toInt()
        val sampleTop = top + (cellHeight * 0.30f).toInt()
        val sampleBottom = bottom - (cellHeight * 0.30f).toInt()
        if (sampleRight <= sampleLeft || sampleBottom <= sampleTop) return null

        val ranges = listOf(
            GridCellType.WALL to config.wallColorRange,
            GridCellType.FREE to config.freeColorRange,
            GridCellType.START to config.startColorRange,
            GridCellType.GOAL to config.goalColorRange
        )
        val counts = IntArray(ranges.size)
        var total = 0
        for (y in sampleTop until sampleBottom) {
            for (x in sampleLeft until sampleRight) {
                val pixel = frame.pixel(x, y)
                ranges.forEachIndexed { index, entry ->
                    if (entry.second.contains(pixel)) counts[index]++
                }
                total++
            }
        }
        if (total == 0) return null

        val ranked = counts.indices.sortedByDescending { counts[it] }
        val bestIndex = ranked[0]
        val bestRatio = counts[bestIndex].toFloat() / total.toFloat()
        val secondRatio = counts[ranked[1]].toFloat() / total.toFloat()
        if (bestRatio < config.minCellColorMatchRatio || bestRatio - secondRatio < 0.25f) return null

        return ranges[bestIndex].first to (bestRatio - secondRatio).toDouble()
    }

    private fun spacingConfidence(spacings: List<Int>): Float {
        if (spacings.isEmpty()) return 0f
        val median = spacings.sorted()[spacings.size / 2].coerceAtLeast(1)
        val meanRelativeDeviation = spacings.map { kotlin.math.abs(it - median).toFloat() / median }
            .average().toFloat()
        return (1f - meanRelativeDeviation).coerceIn(0f, 1f)
    }

    private fun isRegular(spacings: List<Int>): Boolean {
        if (spacings.size < 2) return true
        val median = spacings.sorted()[spacings.size / 2]
        return spacings.all { kotlin.math.abs(it - median) <= maxOf(2, (median * 0.18f).toInt()) }
    }

    private fun luminance(argb: Int): Int =
        (((argb ushr 16) and 0xFF) * 299 + ((argb ushr 8) and 0xFF) * 587 + (argb and 0xFF) * 114) / 1000

    private fun failure(reason: GridRecognitionError, details: String) =
        GridRecognitionResult.Failure(reason, details)
}
