package com.pippo2801.universalgamebot.games

import android.graphics.Bitmap
import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.core.VisionResult

interface GameAdapter {
    val gameId: String
    val gameName: String

    fun detect(visionResult: VisionResult): Boolean
    fun observe(bitmap: Bitmap): Map<String, Any>
    fun planAction(stateData: Map<String, Any>): BotAction
    fun verifyVictory(bitmap: Bitmap): Boolean
    fun verifyFailure(bitmap: Bitmap): Boolean
}
