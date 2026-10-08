package com.pippo2801.universalgamebot.games

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.core.ActionType
import com.pippo2801.universalgamebot.core.VisionResult

class FCMobileAdapter : GameAdapter {
    override val gameId: String = "ea_sports_fc_mobile"
    override val gameName: String = "EA SPORTS FC Mobile"

    override fun detect(visionResult: VisionResult): Boolean = false
    override fun observe(bitmap: Bitmap): Map<String, Any> = emptyMap()
    override fun planAction(stateData: Map<String, Any>): BotAction = BotAction(ActionType.NONE)
    override fun verifyVictory(bitmap: Bitmap): Boolean = false
    override fun verifyFailure(bitmap: Bitmap): Boolean = false
}
