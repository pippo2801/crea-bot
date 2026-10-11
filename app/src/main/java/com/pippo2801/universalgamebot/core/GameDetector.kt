package com.pippo2801.universalgamebot.core

/**
 * No validated visual fingerprints are implemented yet.
 * Returning Unknown is safer than misidentifying any high-confidence screenshot as Amaze GO.
 */
class GameDetector {

    fun detectGame(visionResult: VisionResult): String = "Unknown Game"
}
