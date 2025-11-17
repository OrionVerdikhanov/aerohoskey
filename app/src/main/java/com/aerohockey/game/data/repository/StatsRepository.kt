package com.aerohockey.game.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aerohockey.game.data.model.GameStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Репозиторий для работы со статистикой игр через DataStore
 */
class StatsRepository(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_stats")

        private val TOTAL_GAMES = intPreferencesKey("total_games")
        private val PLAYER1_WINS = intPreferencesKey("player1_wins")
        private val PLAYER2_WINS = intPreferencesKey("player2_wins")
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    /** Flow для чтения статистики */
    val statsFlow: Flow<GameStats> = context.dataStore.data.map { preferences ->
        GameStats(
            totalGames = preferences[TOTAL_GAMES] ?: 0,
            player1Wins = preferences[PLAYER1_WINS] ?: 0,
            player2Wins = preferences[PLAYER2_WINS] ?: 0,
            soundEnabled = preferences[SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true
        )
    }

    /** Сохранение результата игры */
    suspend fun saveGameResult(winnerId: Int) {
        context.dataStore.edit { preferences ->
            val currentTotal = preferences[TOTAL_GAMES] ?: 0
            val currentPlayer1Wins = preferences[PLAYER1_WINS] ?: 0
            val currentPlayer2Wins = preferences[PLAYER2_WINS] ?: 0

            preferences[TOTAL_GAMES] = currentTotal + 1

            if (winnerId == 1) {
                preferences[PLAYER1_WINS] = currentPlayer1Wins + 1
            } else {
                preferences[PLAYER2_WINS] = currentPlayer2Wins + 1
            }
        }
    }

    /** Сброс статистики */
    suspend fun resetStats() {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_GAMES] = 0
            preferences[PLAYER1_WINS] = 0
            preferences[PLAYER2_WINS] = 0
        }
    }

    /** Сохранение настроек звука */
    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    /** Сохранение настроек вибрации */
    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }
}
