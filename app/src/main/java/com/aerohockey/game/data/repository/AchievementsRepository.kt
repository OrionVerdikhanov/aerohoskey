package com.aerohockey.game.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aerohockey.game.R
import com.aerohockey.game.core.config.GameConfig
import com.aerohockey.game.domain.model.Achievement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Репозиторий для управления достижениями
 */
class AchievementsRepository(private val context: Context) {

    companion object {
        private val UNLOCKED_ACHIEVEMENTS_KEY = stringPreferencesKey("unlocked_achievements")
        private val FIRST_GOAL_KEY = intPreferencesKey("achievement_first_goal")
        private val HAT_TRICK_KEY = intPreferencesKey("achievement_hat_trick")
        private val PERFECT_GAME_KEY = intPreferencesKey("achievement_perfect_game")
        private val COMEBACK_KEY = intPreferencesKey("achievement_comeback")
        private val HUNDRED_WINS_KEY = intPreferencesKey("achievement_hundred_wins")
        private val SPEED_DEMON_KEY = intPreferencesKey("achievement_speed_demon")
        private val WALL_MASTER_KEY = intPreferencesKey("achievement_wall_master")
        private val POWER_COLLECTOR_KEY = intPreferencesKey("achievement_power_collector")
    }

    /**
     * Получить все достижения
     */
    fun getAchievements(): Flow<List<Achievement>> {
        return GameConfig.dataStore(context).data.map { preferences ->
            val unlockedIds = preferences[UNLOCKED_ACHIEVEMENTS_KEY]?.split(",") ?: emptyList()

            listOf(
                Achievement(
                    id = Achievement.FIRST_GOAL,
                    titleResId = R.string.achievement_first_blood,
                    descriptionResId = R.string.achievement_first_blood_desc,
                    icon = "⚡",
                    isUnlocked = unlockedIds.contains(Achievement.FIRST_GOAL),
                    progress = preferences[FIRST_GOAL_KEY] ?: 0,
                    maxProgress = 1,
                    color = 0xFFFFD700.toInt()
                ),
                Achievement(
                    id = Achievement.HAT_TRICK,
                    titleResId = R.string.achievement_hat_trick,
                    descriptionResId = R.string.achievement_hat_trick_desc,
                    icon = "🎩",
                    isUnlocked = unlockedIds.contains(Achievement.HAT_TRICK),
                    progress = preferences[HAT_TRICK_KEY] ?: 0,
                    maxProgress = 1,
                    color = 0xFFFF6B6B.toInt()
                ),
                Achievement(
                    id = Achievement.PERFECT_GAME,
                    titleResId = R.string.achievement_perfect,
                    descriptionResId = R.string.achievement_perfect_desc,
                    icon = "💎",
                    isUnlocked = unlockedIds.contains(Achievement.PERFECT_GAME),
                    progress = preferences[PERFECT_GAME_KEY] ?: 0,
                    maxProgress = 1,
                    color = 0xFF4ECDC4.toInt()
                ),
                Achievement(
                    id = Achievement.COMEBACK,
                    titleResId = R.string.achievement_comeback,
                    descriptionResId = R.string.achievement_comeback_desc,
                    icon = "🔥",
                    isUnlocked = unlockedIds.contains(Achievement.COMEBACK),
                    progress = preferences[COMEBACK_KEY] ?: 0,
                    maxProgress = 1,
                    color = 0xFFFF8C42.toInt()
                ),
                Achievement(
                    id = Achievement.HUNDRED_WINS,
                    titleResId = R.string.achievement_hundred,
                    descriptionResId = R.string.achievement_hundred_desc,
                    icon = "👑",
                    isUnlocked = unlockedIds.contains(Achievement.HUNDRED_WINS),
                    progress = preferences[HUNDRED_WINS_KEY] ?: 0,
                    maxProgress = 100,
                    color = 0xFFAE63E4.toInt()
                ),
                Achievement(
                    id = Achievement.SPEED_DEMON,
                    titleResId = R.string.achievement_speed_demon,
                    descriptionResId = R.string.achievement_speed_demon_desc,
                    icon = "🚀",
                    isUnlocked = unlockedIds.contains(Achievement.SPEED_DEMON),
                    progress = preferences[SPEED_DEMON_KEY] ?: 0,
                    maxProgress = 10,
                    color = 0xFF95E1D3.toInt()
                ),
                Achievement(
                    id = Achievement.WALL_MASTER,
                    titleResId = R.string.achievement_wall_master,
                    descriptionResId = R.string.achievement_wall_master_desc,
                    icon = "🧱",
                    isUnlocked = unlockedIds.contains(Achievement.WALL_MASTER),
                    progress = preferences[WALL_MASTER_KEY] ?: 0,
                    maxProgress = 50,
                    color = 0xFFFAA916.toInt()
                ),
                Achievement(
                    id = Achievement.POWER_COLLECTOR,
                    titleResId = R.string.achievement_power_collector,
                    descriptionResId = R.string.achievement_power_collector_desc,
                    icon = "✨",
                    isUnlocked = unlockedIds.contains(Achievement.POWER_COLLECTOR),
                    progress = preferences[POWER_COLLECTOR_KEY] ?: 0,
                    maxProgress = 20,
                    color = 0xFFF38181.toInt()
                )
            )
        }
    }

    /**
     * Разблокировать достижение
     */
    suspend fun unlockAchievement(achievementId: String) {
        GameConfig.dataStore(context).edit { preferences ->
            val current = preferences[UNLOCKED_ACHIEVEMENTS_KEY] ?: ""
            val ids = current.split(",").toMutableSet()
            ids.add(achievementId)
            preferences[UNLOCKED_ACHIEVEMENTS_KEY] = ids.filter { it.isNotEmpty() }.joinToString(",")
        }
    }

    /**
     * Обновить прогресс достижения
     */
    suspend fun updateProgress(achievementId: String, progress: Int) {
        GameConfig.dataStore(context).edit { preferences ->
            val key = when (achievementId) {
                Achievement.FIRST_GOAL -> FIRST_GOAL_KEY
                Achievement.HAT_TRICK -> HAT_TRICK_KEY
                Achievement.PERFECT_GAME -> PERFECT_GAME_KEY
                Achievement.COMEBACK -> COMEBACK_KEY
                Achievement.HUNDRED_WINS -> HUNDRED_WINS_KEY
                Achievement.SPEED_DEMON -> SPEED_DEMON_KEY
                Achievement.WALL_MASTER -> WALL_MASTER_KEY
                Achievement.POWER_COLLECTOR -> POWER_COLLECTOR_KEY
                else -> return@edit
            }
            preferences[key] = progress
        }
    }

    /**
     * Сбросить все достижения
     */
    suspend fun resetAchievements() {
        GameConfig.dataStore(context).edit { preferences ->
            preferences.remove(UNLOCKED_ACHIEVEMENTS_KEY)
            preferences.remove(FIRST_GOAL_KEY)
            preferences.remove(HAT_TRICK_KEY)
            preferences.remove(PERFECT_GAME_KEY)
            preferences.remove(COMEBACK_KEY)
            preferences.remove(HUNDRED_WINS_KEY)
            preferences.remove(SPEED_DEMON_KEY)
            preferences.remove(WALL_MASTER_KEY)
            preferences.remove(POWER_COLLECTOR_KEY)
        }
    }
}
