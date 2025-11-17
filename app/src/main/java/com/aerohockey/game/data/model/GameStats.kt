package com.aerohockey.game.data.model

/**
 * Модель статистики игр
 */
data class GameStats(
    val totalGames: Int = 0,
    val player1Wins: Int = 0,
    val player2Wins: Int = 0,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    /** Процент побед игрока 1 */
    val player1WinRate: Int
        get() = if (totalGames > 0) (player1Wins * 100 / totalGames) else 0

    /** Процент побед игрока 2 */
    val player2WinRate: Int
        get() = if (totalGames > 0) (player2Wins * 100 / totalGames) else 0
}
