package com.aerohockey.game.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aerohockey.game.core.config.GameConfig
import com.aerohockey.game.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Репозиторий для прогресса игрока (уровень, опыт, монеты, квесты)
 */
class PlayerProgressRepository(private val context: Context) {

    companion object {
        // Уровень и опыт
        private val PLAYER_LEVEL_KEY = intPreferencesKey("player_level")
        private val PLAYER_XP_KEY = intPreferencesKey("player_xp")
        private val PLAYER_TOTAL_XP_KEY = intPreferencesKey("player_total_xp")

        // Монеты
        private val PLAYER_COINS_KEY = intPreferencesKey("player_coins")

        // Квесты
        private val DAILY_QUESTS_KEY = stringPreferencesKey("daily_quests")
        private val QUESTS_RESET_TIME_KEY = longPreferencesKey("quests_reset_time")

        // Скины
        private val UNLOCKED_SKINS_KEY = stringPreferencesKey("unlocked_skins")
        private val EQUIPPED_PUCK_SKIN_KEY = stringPreferencesKey("equipped_puck_skin")
        private val EQUIPPED_PADDLE_SKIN_KEY = stringPreferencesKey("equipped_paddle_skin")
    }

    /**
     * Получить уровень игрока
     */
    fun getPlayerLevel(): Flow<PlayerLevel> {
        return GameConfig.dataStore(context).data.map { preferences ->
            PlayerLevel(
                currentLevel = preferences[PLAYER_LEVEL_KEY] ?: 1,
                currentXP = preferences[PLAYER_XP_KEY] ?: 0,
                totalXP = preferences[PLAYER_TOTAL_XP_KEY] ?: 0
            )
        }
    }

    /**
     * Сохранить уровень игрока
     */
    suspend fun savePlayerLevel(level: PlayerLevel) {
        GameConfig.dataStore(context).edit { preferences ->
            preferences[PLAYER_LEVEL_KEY] = level.currentLevel
            preferences[PLAYER_XP_KEY] = level.currentXP
            preferences[PLAYER_TOTAL_XP_KEY] = level.totalXP
        }
    }

    /**
     * Добавить опыт
     */
    suspend fun addXP(amount: Int, multiplier: Float = 1f): LevelUpResult {
        val currentLevel = getPlayerLevel().first()
        val result = currentLevel.addXP(amount, multiplier)
        savePlayerLevel(currentLevel)
        return result
    }

    /**
     * Получить монеты
     */
    fun getCoins(): Flow<Int> {
        return GameConfig.dataStore(context).data.map { preferences ->
            preferences[PLAYER_COINS_KEY] ?: 0
        }
    }

    /**
     * Добавить монеты
     */
    suspend fun addCoins(amount: Int) {
        GameConfig.dataStore(context).edit { preferences ->
            val current = preferences[PLAYER_COINS_KEY] ?: 0
            preferences[PLAYER_COINS_KEY] = current + amount
        }
    }

    /**
     * Потратить монеты
     */
    suspend fun spendCoins(amount: Int): Boolean {
        val current = getCoins().first()
        return if (current >= amount) {
            GameConfig.dataStore(context).edit { preferences ->
                preferences[PLAYER_COINS_KEY] = current - amount
            }
            true
        } else {
            false
        }
    }

    /**
     * Получить ежедневные задания
     */
    suspend fun getDailyQuests(): List<DailyQuest> {
        // Проверить, нужно ли сбросить задания
        checkAndResetQuests()

        val preferences = GameConfig.dataStore(context).data.first()
        val questsJson = preferences[DAILY_QUESTS_KEY]

        return if (questsJson.isNullOrEmpty()) {
            // Создать новые задания
            val newQuests = DailyQuest.createDailySet(3)
            saveDailyQuests(newQuests)
            newQuests
        } else {
            // Десериализовать из JSON (упрощенно)
            DailyQuest.createDailySet(3) // TODO: реализовать сериализацию
        }
    }

    /**
     * Сохранить ежедневные задания
     */
    private suspend fun saveDailyQuests(quests: List<DailyQuest>) {
        GameConfig.dataStore(context).edit { preferences ->
            // TODO: сериализовать в JSON
            preferences[DAILY_QUESTS_KEY] = ""
        }
    }

    /**
     * Проверить и сбросить задания
     */
    private suspend fun checkAndResetQuests() {
        val preferences = GameConfig.dataStore(context).data.first()
        val resetTime = preferences[QUESTS_RESET_TIME_KEY] ?: 0L
        val currentTime = System.currentTimeMillis()

        // Сброс каждые 24 часа
        if (currentTime - resetTime > 24 * 60 * 60 * 1000) {
            GameConfig.dataStore(context).edit { prefs ->
                prefs[DAILY_QUESTS_KEY] = ""
                prefs[QUESTS_RESET_TIME_KEY] = currentTime
            }
        }
    }

    /**
     * Обновить прогресс задания
     */
    suspend fun updateQuestProgress(questType: QuestType, amount: Int = 1) {
        // TODO: реализовать обновление конкретного задания
    }

    /**
     * Получить награду за задание
     */
    suspend fun claimQuestReward(questId: String): QuestReward? {
        // TODO: реализовать получение награды
        return null
    }

    /**
     * Получить разблокированные скины
     */
    fun getUnlockedSkins(): Flow<List<String>> {
        return GameConfig.dataStore(context).data.map { preferences ->
            val skinsStr = preferences[UNLOCKED_SKINS_KEY] ?: "puck_default,paddle_default"
            skinsStr.split(",").filter { it.isNotEmpty() }
        }
    }

    /**
     * Разблокировать скин
     */
    suspend fun unlockSkin(skinId: String) {
        GameConfig.dataStore(context).edit { preferences ->
            val current = preferences[UNLOCKED_SKINS_KEY] ?: "puck_default,paddle_default"
            val skins = current.split(",").toMutableSet()
            skins.add(skinId)
            preferences[UNLOCKED_SKINS_KEY] = skins.joinToString(",")
        }
    }

    /**
     * Экипировать скин
     */
    suspend fun equipSkin(skin: Skin) {
        GameConfig.dataStore(context).edit { preferences ->
            when (skin.type) {
                SkinType.PUCK -> preferences[EQUIPPED_PUCK_SKIN_KEY] = skin.id
                SkinType.PADDLE -> preferences[EQUIPPED_PADDLE_SKIN_KEY] = skin.id
            }
        }
    }

    /**
     * Получить экипированный скин
     */
    suspend fun getEquippedSkin(type: SkinType): String {
        val preferences = GameConfig.dataStore(context).data.first()
        return when (type) {
            SkinType.PUCK -> preferences[EQUIPPED_PUCK_SKIN_KEY] ?: "puck_default"
            SkinType.PADDLE -> preferences[EQUIPPED_PADDLE_SKIN_KEY] ?: "paddle_default"
        }
    }
}
