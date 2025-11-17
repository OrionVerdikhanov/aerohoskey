package com.aerohockey.game.domain.model

/**
 * Состояние игры
 */
sealed class GameState {
    /** Игра не началась */
    object NotStarted : GameState()

    /** Обратный отсчет перед стартом */
    data class Countdown(val seconds: Int) : GameState()

    /** Игра идет */
    object Playing : GameState()

    /** Пауза */
    object Paused : GameState()

    /** Гол забит */
    data class GoalScored(val scoringPlayer: Int) : GameState()

    /** Игра окончена */
    data class Finished(val winnerId: Int) : GameState()
}

/**
 * Данные игровой сессии
 */
data class GameSession(
    var player1Score: Int = 0,
    var player2Score: Int = 0,
    var state: GameState = GameState.NotStarted,
    val maxScore: Int = 7 // Побеждает первый, кто наберет это количество очков
) {
    /** Добавление очка игроку */
    fun addScore(playerId: Int) {
        if (playerId == 1) {
            player1Score++
        } else {
            player2Score++
        }

        // Проверка на победу
        if (player1Score >= maxScore) {
            state = GameState.Finished(1)
        } else if (player2Score >= maxScore) {
            state = GameState.Finished(2)
        } else {
            state = GameState.GoalScored(playerId)
        }
    }

    /** Сброс игры */
    fun reset() {
        player1Score = 0
        player2Score = 0
        state = GameState.NotStarted
    }

    /** Получение победителя */
    fun getWinner(): Int? {
        return when (state) {
            is GameState.Finished -> (state as GameState.Finished).winnerId
            else -> null
        }
    }
}
