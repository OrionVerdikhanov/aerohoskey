package com.aerohockey.game.domain.model

/**
 * Система уровней и опыта
 */
data class PlayerLevel(
    var currentLevel: Int = 1,
    var currentXP: Int = 0,
    var totalXP: Int = 0
) {
    /**
     * Добавить опыт
     */
    fun addXP(amount: Int, multiplier: Float = 1f): LevelUpResult {
        val xpToAdd = (amount * multiplier).toInt()
        currentXP += xpToAdd
        totalXP += xpToAdd

        val leveledUp = checkLevelUp()

        return LevelUpResult(
            xpGained = xpToAdd,
            leveledUp = leveledUp,
            newLevel = currentLevel,
            totalXP = totalXP
        )
    }

    /**
     * Проверить повышение уровня
     */
    private fun checkLevelUp(): Boolean {
        var leveledUp = false
        while (currentXP >= getXPForNextLevel()) {
            currentXP -= getXPForNextLevel()
            currentLevel++
            leveledUp = true
        }
        return leveledUp
    }

    /**
     * Получить XP для следующего уровня
     */
    fun getXPForNextLevel(): Int {
        // Формула: 100 * level^1.5
        return (100 * Math.pow(currentLevel.toDouble(), 1.5)).toInt()
    }

    /**
     * Получить прогресс до следующего уровня (0-100)
     */
    fun getLevelProgress(): Int {
        val xpNeeded = getXPForNextLevel()
        return ((currentXP.toFloat() / xpNeeded) * 100).toInt()
    }

    /**
     * Получить ранг игрока
     */
    fun getRank(): PlayerRank {
        return when (currentLevel) {
            in 1..9 -> PlayerRank.ROOKIE
            in 10..24 -> PlayerRank.AMATEUR
            in 25..49 -> PlayerRank.PROFESSIONAL
            in 50..99 -> PlayerRank.EXPERT
            in 100..Int.MAX_VALUE -> PlayerRank.MASTER
            else -> PlayerRank.ROOKIE
        }
    }

    companion object {
        // XP награды
        const val XP_GOAL_SCORED = 10
        const val XP_WIN_MATCH = 50
        const val XP_PERFECT_GAME = 100
        const val XP_POWER_UP_COLLECTED = 5
        const val XP_ACHIEVEMENT_UNLOCKED = 200
    }
}

/**
 * Ранги игрока
 */
enum class PlayerRank(val icon: String, val nameResId: Int) {
    ROOKIE("🥉", 0),
    AMATEUR("🥈", 0),
    PROFESSIONAL("🥇", 0),
    EXPERT("💎", 0),
    MASTER("👑", 0)
}

/**
 * Результат получения опыта
 */
data class LevelUpResult(
    val xpGained: Int,
    val leveledUp: Boolean,
    val newLevel: Int,
    val totalXP: Int
)
