package com.pippo2801.universalgamebot.games

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.core.ActionType
import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.core.VisionResult
import com.pippo2801.universalgamebot.games.GridPathPlanner
import com.pippo2801.universalgamebot.vision.GridImageRecognizer
import com.pippo2801.universalgamebot.vision.GridRecognitionResult

/**
 * Safe Amaze GO adapter. A recognizer can be injected after its visual ranges have been calibrated.
 * Recognition remains observation-only: this adapter never treats a grid as authorization to act.
 */
class AmazeAdapter(
    private val gridRecognizer: GridImageRecognizer? = null
) : GameAdapter {
    override val gameId: String = "amaze_go"
    override val gameName: String = "Amaze GO"

    // Generic metadata is not a validated game fingerprint.
    override fun detect(visionResult: VisionResult): Boolean = false

    override fun observe(bitmap: Bitmap): Map<String, Any> {
        val recognizer = gridRecognizer ?: return mapOf(
            "width" to bitmap.width,
            "height" to bitmap.height,
            "grid" to "not_recognized",
            "recognitionStatus" to "recognizer_not_configured",
            "readyForAction" to false
        )

        return when (val result = recognizer.recognize(bitmap)) {
            is GridRecognitionResult.Success -> {
                val observation = result.grid.observation
                val planning = GridPathPlanner.planValidated(observation)
                val base = mutableMapOf<String, Any>(
                    "width" to bitmap.width,
                    "height" to bitmap.height,
                    "grid" to result.grid.rows,
                    "gridConfidence" to result.grid.confidence,
                    "gridBounds" to result.grid.region,
                    "gridObservation" to observation,
                    "recognitionStatus" to "recognized_observation_only",
                    "readyForAction" to false
                )
                if (planning.isSuccess) {
                    val plan = requireNotNull(planning.plan)
                    base["pathPlanStatus"] = "planned_observation_only"
                    base["plannedGridPath"] = plan.path
                    base["plannedScreenPoints"] = plan.screenPoints
                } else {
                    base["pathPlanStatus"] = "blocked_" + (planning.failure?.name ?: "UNKNOWN")
                }
                base
            }
            is GridRecognitionResult.Failure -> mapOf(
                "width" to bitmap.width,
                "height" to bitmap.height,
                "grid" to "not_recognized",
                "recognitionStatus" to result.reason.name,
                "recognitionDetails" to result.details,
                "readyForAction" to false
            )
        }
    }

    override fun planAction(stateData: Map<String, Any>): BotAction =
        BotAction(
            type = ActionType.NONE,
            description = "Observation only: recognition is not calibrated or autonomy-certified"
        )

    override fun verifyVictory(bitmap: Bitmap): Boolean = false
    override fun verifyFailure(bitmap: Bitmap): Boolean = false
}
