package com.pippo2801.universalgamebot.games

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

    fun updateProfile(profile: GameProfile) {
        // Do not allow callers to infer verified autonomy from a score alone.
        profiles[profile.gameId] = if (profile.isVerified &&
            profile.autonomyStatus == AutonomyStatus.AUTONOMY_VERIFIED &&
            profile.lastVerifiedAt > 0L &&
            profile.learningScore >= 0 &&
            profile.learningScore <= 100
        ) {
            profile
        } else {
            profile.copy(
                isVerified = false,
                autonomyStatus = if (profile.autonomyStatus == AutonomyStatus.NOT_AUTONOMOUS)
                    AutonomyStatus.NOT_AUTONOMOUS else AutonomyStatus.OBSERVATION,
                lastVerifiedAt = 0L
            )
        }
    }
}
