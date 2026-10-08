package com.pippo2801.universalgamebot.planner

import com.pippo2801.universalgamebot.core.BotAction
import com.pippo2801.universalgamebot.games.GameAdapter

class Planner {
    fun planNextAction(adapter: GameAdapter, stateData: Map<String, Any>): BotAction {
        return adapter.planAction(stateData)
    }
}
