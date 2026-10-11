package com.pippo2801.universalgamebot.games

import com.pippo2801.universalgamebot.verification.AutonomyCertificate

class GameRegistry {

    private val profiles = mutableMapOf<String, GameProfile>()

    init {
        // Registration is a catalogue only; it does not imply an adapter is functional.
        val initialGames = listOf(
            "Sudoku",
            "Crypto Game",
            "Palmon: Survival",
            "Palmon World",
            "Word Search",
            "Kitchen...",
            "Paradise...",
            "Dice Dreams",
            "Yarn Loop!",
            "Box A...",
            "Solitaire",
            "Weapon...",
            "Arrow Escape",
            "Fight For World",
            "Mahjong...",
            "Block Bus!",
            "Crown C...",
            "Number Match",
            "WeScrabble / WordScaper",
            "We Are Warriors!",
            "Tennis Clash",
            "Coin Master",
            "Monopoly",
            "EA SPORTS FC Mobile",
            "Amaze GO"
        )

        for (game in initialGames) {
            val id = game.lowercase().replace(Regex("[^a-z0-9]"), "_")
            profiles[id] = GameProfile(
                gameId = id,
                name = game,
                learningScore = 0,
                confidence = 0.0f,
                autonomyStatus = AutonomyStatus.OBSERVATION,
                lastVerifiedAt = 0L,
                strategiesCount = 0,
                errorCount = 0,
                isVerified = false
            )
        }
    }

    fun getAllProfiles(): List<GameProfile> = profiles.values.toList()

    fun getProfile(gameId: String): GameProfile? = profiles[gameId]

    /** Ordinary profile updates cannot assert that a game is autonomous. */
    fun updateProfile(profile: GameProfile) {
        val score = profile.learningScore.coerceIn(0, 100)
        val status = when {
            profile.autonomyStatus == AutonomyStatus.NOT_AUTONOMOUS -> AutonomyStatus.NOT_AUTONOMOUS
            score < 40 -> AutonomyStatus.OBSERVATION
            score < 70 -> AutonomyStatus.LEARNING
            score < 90 -> AutonomyStatus.AUTONOMOUS_TESTING
            else -> AutonomyStatus.NEARLY_READY
        }
        profiles[profile.gameId] = profile.copy(
            learningScore = score,
            isVerified = false,
            confidence = profile.confidence.coerceIn(0f, 1f),
            autonomyStatus = status,
            lastVerifiedAt = 0L
        )
    }

    /** Only a complete, successful certificate matching this exact profile can verify it. */
    fun applyCertificate(profile: GameProfile, certificate: AutonomyCertificate): Boolean {
        val valid = certificate.gameId == profile.gameId &&
            certificate.profileVersion == profile.version &&
            certificate.issuedAt > 0L &&
            certificate.totalTests > 0 &&
            certificate.passedTests == certificate.totalTests &&
            certificate.failedTests == 0 &&
            certificate.confidence >= 1.0f &&
            certificate.isVerified

        if (!valid) {
            updateProfile(profile)
            return false
        }

        profiles[profile.gameId] = profile.copy(
            isVerified = true,
            confidence = 1.0f,
            autonomyStatus = AutonomyStatus.AUTONOMY_VERIFIED,
            lastVerifiedAt = certificate.issuedAt
        )
        return true
    }
}
