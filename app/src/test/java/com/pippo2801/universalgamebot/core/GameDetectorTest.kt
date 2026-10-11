package com.pippo2801.universalgamebot.core

import org.junit.Assert.assertEquals
import org.junit.Test

class GameDetectorTest {

    @Test
    fun doesNotGuessGameFromGenericConfidence() {
        val detector = GameDetector()
        val genericScreen = VisionResult(
            confidence = 0.99f,
            detectedObjects = emptyList(),
            dominantColor = 0,
            stateSummary = "generic screen"
        )

        assertEquals("Unknown Game", detector.detectGame(genericScreen))
    }
}
