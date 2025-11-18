package com.aerohockey.game.domain.model

/**
 * Система комбо для последовательных голов
 */
data class ComboSystem(
    var currentCombo: Int = 0,
    var maxCombo: Int = 0,
    var lastScoringPlayer: Int = 0
) {
    /**
     * Добавить гол в комбо
     */
    fun addGoal(playerId: Int): ComboResult {
        return if (playerId == lastScoringPlayer) {
            // Продолжение комбо
            currentCombo++
            if (currentCombo > maxCombo) {
                maxCombo = currentCombo
            }
            ComboResult(
                combo = currentCombo,
                multiplier = getMultiplier(),
                isNew = false,
                message = getComboMessage()
            )
        } else {
            // Новое комбо
            currentCombo = 1
            lastScoringPlayer = playerId
            ComboResult(
                combo = 1,
                multiplier = 1f,
                isNew = true,
                message = null
            )
        }
    }

    /**
     * Сброс комбо
     */
    fun reset() {
        currentCombo = 0
        lastScoringPlayer = 0
    }

    /**
     * Получить мультипликатор опыта
     */
    fun getMultiplier(): Float {
        return when (currentCombo) {
            in 2..3 -> 1.5f
            in 4..5 -> 2.0f
            in 6..7 -> 2.5f
            in 8..Int.MAX_VALUE -> 3.0f
            else -> 1.0f
        }
    }

    /**
     * Получить сообщение комбо
     */
    private fun getComboMessage(): String? {
        return when (currentCombo) {
            2 -> "DOUBLE!"
            3 -> "TRIPLE!"
            4 -> "QUAD!"
            5 -> "MEGA!"
            6 -> "ULTRA!"
            7 -> "MONSTER!"
            8 -> "LEGENDARY!"
            in 9..Int.MAX_VALUE -> "GODLIKE!"
            else -> null
        }
    }
}

/**
 * Результат комбо
 */
data class ComboResult(
    val combo: Int,
    val multiplier: Float,
    val isNew: Boolean,
    val message: String?
)
