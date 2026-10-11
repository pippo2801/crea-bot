package com.pippo2801.universalgamebot.games

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.core.ActionType
import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.core.VisionResult

/**
 * Safe adapter scaffold. Generic screen confidence is not enough to identify Amaze GO.
 * The adapter remains observation-only until grid recognition and path planning exist.
 */
class AmazeAdapter : GameAdapter {
    override val gameId: String = "amaze_go"
    override val gameName: String = "Amaze GO"

    override fun detect(visionResult: VisionResult): Boolean = false

    override fun observe(bitmap: Bitmap): Map<String, Any> = mapOf(
        "width" to bitmap.width,
        "height" to bitmap.height,
        "grid" to "not_recognized",
        "readyForAction" to false
    )

    override fun planAction(stateData: Map<String, Any>): BotAction =
        BotAction(
            type = ActionType.NONE,
            description = "Observation only: maze grid and safe path are not verified"
        )

    override fun verifyVictory(bitmap: Bitmap): Boolean = false
    override fun verifyFailure(bitmap: Bitmap): Boolean = false
}
