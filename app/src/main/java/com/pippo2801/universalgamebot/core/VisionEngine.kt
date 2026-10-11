package com.pippo2801.universalgamebot.core

import android.graphics.Bitmap
import android.graphics.Color

data class VisionResult(
    val confidence: Float,
    val detectedObjects: List<DetectedObject>,
    val dominantColor: Int,
    val stateSummary: String
)

data class DetectedObject(
    val name: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val confidence: Float
)

/**
 * Offline fallback that reports basic image metadata only.
 * A pixel sample is not game recognition, so confidence stays zero and no semantic
 * objects are fabricated. A real recognizer can replace this implementation later.
 */
class VisionEngine {

    fun analyzeBitmap(bitmap: Bitmap?): VisionResult {
        if (bitmap == null || bitmap.width <= 0 || bitmap.height <= 0) {
            return VisionResult(
                confidence = 0.0f,
                detectedObjects = emptyList(),
                dominantColor = Color.BLACK,
                stateSummary = "No valid bitmap; recognition unavailable"
            )
        }

        val sampleX = bitmap.width / 2
        val sampleY = bitmap.height / 2
        val sampledPixel = bitmap.getPixel(sampleX, sampleY)
        val brightness = (
            Color.red(sampledPixel) +
            Color.green(sampledPixel) +
            Color.blue(sampledPixel)
        ) / 3

        return VisionResult(
            confidence = 0.0f,
            detectedObjects = emptyList(),
            dominantColor = sampledPixel,
            stateSummary = "Image metadata only (" + bitmap.width + "x" + bitmap.height +
                ", sampled brightness " + brightness + "); game/object recognition unavailable"
        )
    }
}
