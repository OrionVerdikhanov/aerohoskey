package com.aerohockey.game.core.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Центральная конфигурация игровых параметров
 * Все константы игры собраны в одном месте для удобства настройки
 */
object GameConfig {

    // DataStore для хранения настроек и прогресса
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "game_prefs")

    fun dataStore(context: Context): DataStore<Preferences> = context.dataStore

    // Физика шайбы
    const val FRICTION = 0.98f
    const val RESTITUTION = 0.9f
    const val PADDLE_BOOST = 1.5f
    const val MAX_PUCK_SPEED = 1500f
    const val MIN_PUCK_SPEED = 50f

    // Размеры объектов (коэффициенты от размера экрана)
    const val PUCK_RADIUS_RATIO = 0.02f
    const val PADDLE_RADIUS_RATIO = 0.04f
    const val GOAL_WIDTH_RATIO = 0.4f
    const val GOAL_HEIGHT_RATIO = 0.02f

    // Игровой цикл
    const val TARGET_FPS = 60
    const val TARGET_FRAME_TIME = 1000L / TARGET_FPS

    // Счет и победа
    const val DEFAULT_MAX_SCORE = 7
    const val TOURNAMENT_MAX_SCORE = 15
    const val QUICK_MATCH_SCORE = 3

    // Задержки
    const val GOAL_CELEBRATION_DELAY = 2000L
    const val COUNTDOWN_START_SECONDS = 3

    // AI параметры
    object AI {
        const val REACTION_TIME_EASY = 0.3f
        const val REACTION_TIME_MEDIUM = 0.15f
        const val REACTION_TIME_HARD = 0.05f

        const val PREDICTION_EASY = 0.5f
        const val PREDICTION_MEDIUM = 0.8f
        const val PREDICTION_HARD = 1.0f

        const val ERROR_MARGIN_EASY = 100f
        const val ERROR_MARGIN_MEDIUM = 50f
        const val ERROR_MARGIN_HARD = 10f
    }

    // Power-ups
    object PowerUps {
        const val SPAWN_INTERVAL = 20000L
        const val DURATION = 5000L
        const val SPEED_MULTIPLIER = 1.5f
        const val SIZE_MULTIPLIER = 1.3f
        const val FREEZE_DURATION = 500L
    }

    // Звуки
    object Audio {
        const val MASTER_VOLUME = 1.0f
        const val EFFECT_VOLUME = 0.8f
        const val MAX_STREAMS = 5
    }

    // Вибрация
    object Haptic {
        const val PADDLE_HIT_DURATION = 50L
        const val WALL_HIT_DURATION = 30L
        const val GOAL_DURATION = 200L
    }

    // Достижения
    object Achievements {
        const val FIRST_BLOOD = 1      // Первый гол
        const val HAT_TRICK = 3        // 3 гола подряд
        const val PERFECT_GAME = 7     // Победа без пропущенных
        const val COMEBACK = 5         // Победа с отставанием в 5 голов
        const val HUNDRED_WINS = 100   // 100 побед
    }

    // Реклама
    object Ads {
        const val INTERSTITIAL_FREQUENCY = 3 // Каждые N игр
        const val MIN_INTERVAL_MS = 180000L  // 3 минуты между показами
    }
}
