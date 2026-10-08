package com.pippo2801.universalgamebot.games

class GameRegistry {

    private val profiles = mutableMapOf<String, GameProfile>()

    init {
        // Initialize default game profiles as requested
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
                learningScore = if (game == "Amaze GO") 45 else 10,
                autonomyStatus = if (game == "Amaze GO") AutonomyStatus.LEARNING else AutonomyStatus.OBSERVATION
            )
        }
    }

    fun getAllProfiles(): List<GameProfile> = profiles.values.toList()

    fun getProfile(gameId: String): GameProfile? = profiles[gameId]

    fun updateProfile(profile: GameProfile) {
        profiles[profile.gameId] = profile
    }
}
