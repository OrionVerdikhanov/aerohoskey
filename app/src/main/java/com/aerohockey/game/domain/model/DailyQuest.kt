package com.aerohockey.game.domain.model

/**
 * Ежедневное задание
 */
data class DailyQuest(
    val id: String,
    val type: QuestType,
    val target: Int,
    var progress: Int = 0,
    val reward: QuestReward,
    var isCompleted: Boolean = false,
    var isClaimed: Boolean = false,
    val expiresAt: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 часа
) {
    /**
     * Обновить прогресс
     */
    fun updateProgress(amount: Int = 1): Boolean {
        if (isCompleted) return false

        progress += amount
        if (progress >= target) {
            progress = target
            isCompleted = true
            return true
        }
        return false
    }

    /**
     * Получить прогресс в процентах
     */
    fun getProgressPercent(): Int {
        return ((progress.toFloat() / target) * 100).toInt().coerceIn(0, 100)
    }

    /**
     * Получить награду
     */
    fun claimReward(): QuestReward? {
        return if (isCompleted && !isClaimed) {
            isClaimed = true
            reward
        } else {
            null
        }
    }

    /**
     * Проверить истечение срока
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresAt
    }

    companion object {
        /**
         * Создать случайное задание
         */
        fun createRandom(): DailyQuest {
            val types = QuestType.values()
            val type = types.random()

            return when (type) {
                QuestType.SCORE_GOALS -> DailyQuest(
                    id = "daily_goals_${System.currentTimeMillis()}",
                    type = type,
                    target = listOf(5, 10, 15).random(),
                    reward = QuestReward(xp = 100, coins = 50)
                )
                QuestType.WIN_MATCHES -> DailyQuest(
                    id = "daily_wins_${System.currentTimeMillis()}",
                    type = type,
                    target = listOf(2, 3, 5).random(),
                    reward = QuestReward(xp = 150, coins = 75)
                )
                QuestType.COLLECT_POWERUPS -> DailyQuest(
                    id = "daily_powerups_${System.currentTimeMillis()}",
                    type = type,
                    target = listOf(10, 15, 20).random(),
                    reward = QuestReward(xp = 80, coins = 40)
                )
                QuestType.GET_COMBO -> DailyQuest(
                    id = "daily_combo_${System.currentTimeMillis()}",
                    type = type,
                    target = listOf(3, 5, 7).random(),
                    reward = QuestReward(xp = 200, coins = 100)
                )
                QuestType.PLAY_MATCHES -> DailyQuest(
                    id = "daily_matches_${System.currentTimeMillis()}",
                    type = type,
                    target = listOf(3, 5, 10).random(),
                    reward = QuestReward(xp = 120, coins = 60)
                )
            }
        }

        /**
         * Создать набор ежедневных заданий
         */
        fun createDailySet(count: Int = 3): List<DailyQuest> {
            return (1..count).map { createRandom() }
        }
    }
}

/**
 * Типы заданий
 */
enum class QuestType(val titleResId: Int, val icon: String) {
    SCORE_GOALS(0, "⚽"),
    WIN_MATCHES(0, "🏆"),
    COLLECT_POWERUPS(0, "✨"),
    GET_COMBO(0, "🔥"),
    PLAY_MATCHES(0, "🎮")
}

/**
 * Награда за задание
 */
data class QuestReward(
    val xp: Int = 0,
    val coins: Int = 0
)
