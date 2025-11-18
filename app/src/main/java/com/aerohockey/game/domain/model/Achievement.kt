package com.aerohockey.game.domain.model

/**
 * Модель достижения
 */
data class Achievement(
    val id: String,
    val titleResId: Int,
    val descriptionResId: Int,
    val icon: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val color: Int
) {
    /**
     * Процент прогресса
     */
    fun getProgressPercent(): Int {
        return if (maxProgress > 0) {
            (progress * 100) / maxProgress
        } else {
            0
        }
    }

    /**
     * Завершено ли достижение
     */
    fun isCompleted(): Boolean = progress >= maxProgress

    companion object {
        // ID достижений
        const val FIRST_GOAL = "first_goal"
        const val HAT_TRICK = "hat_trick"
        const val PERFECT_GAME = "perfect_game"
        const val COMEBACK = "comeback"
        const val HUNDRED_WINS = "hundred_wins"
        const val SPEED_DEMON = "speed_demon"
        const val WALL_MASTER = "wall_master"
        const val POWER_COLLECTOR = "power_collector"
    }
}
