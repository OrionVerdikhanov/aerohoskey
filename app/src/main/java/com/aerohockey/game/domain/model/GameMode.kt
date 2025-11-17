package com.aerohockey.game.domain.model

/**
 * Режимы игры
 */
sealed class GameMode {
    /** Игра вдвоем на одном устройстве */
    object TwoPlayers : GameMode()

    /** Игра против AI */
    data class VsAI(val difficulty: Difficulty) : GameMode()

    /** Турнирный режим (играть до N побед) */
    data class Tournament(val maxWins: Int) : GameMode()
}

/**
 * Уровни сложности
 */
enum class Difficulty(
    val reactionTime: Float,
    val predictionAccuracy: Float,
    val errorMargin: Float,
    val speedMultiplier: Float
) {
    EASY(0.3f, 0.5f, 100f, 0.8f),
    MEDIUM(0.15f, 0.8f, 50f, 1.0f),
    HARD(0.05f, 1.0f, 10f, 1.2f),
    EXTREME(0.02f, 1.0f, 5f, 1.5f);

    companion object {
        fun fromOrdinal(ordinal: Int): Difficulty {
            return values().getOrNull(ordinal) ?: MEDIUM
        }
    }
}
