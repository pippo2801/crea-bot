package com.pippo2801.universalgamebot.games

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.core.ActionType
import com.pippo2801.universalgamebot.core.VisionResult

class AmazeAdapter : GameAdapter {
    override val gameId: String = "amaze_go"
    override val gameName: String = "Amaze GO"

    override fun detect(visionResult: VisionResult): Boolean {
        return visionResult.confidence > 0.5f
    }

    override fun observe(bitmap: Bitmap): Map<String, Any> {
        return mapOf("width" to bitmap.width, "height" to bitmap.height, "grid" to "dynamic")
    }

    override fun planAction(stateData: Map<String, Any>): BotAction {
        // BFS / maze sliding swipe action
        return BotAction(
            type = ActionType.SWIPE,
            x1 = 540f, y1 = 1200f,
            x2 = 540f, y2 = 800f,
            duration = 150L,
            description = "Slide Up"
        )
    }

    override fun verifyVictory(bitmap: Bitmap): Boolean = false
    override fun verifyFailure(bitmap: Bitmap): Boolean = false
}
