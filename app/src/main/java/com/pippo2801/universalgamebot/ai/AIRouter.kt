package com.pippo2801.universalgamebot.ai

class ZeroCostGuard {
    var isCloudAiEnabled: Boolean = false
        private set

    fun canUseCloudAi(): Boolean = isCloudAiEnabled
}

class AIRouter(private val zeroCostGuard: ZeroCostGuard) {

    fun queryAi(prompt: String): String {
        if (!zeroCostGuard.canUseCloudAi()) {
            return "Local AI / Rule-based engine fallback (Zero-Cost Guard active)."
        }
        return "Cloud AI response (Configured)"
    }
}
