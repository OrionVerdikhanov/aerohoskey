package com.aerohockey.game.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.aerohockey.game.R
import com.aerohockey.game.core.config.GameConfig

/**
 * Менеджер звуковых эффектов
 * Управляет загрузкой и воспроизведением звуков игры
 */
class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<SoundType, Int>()
    private var isEnabled = true
    private var volume = GameConfig.Audio.EFFECT_VOLUME

    enum class SoundType {
        PADDLE_HIT,
        WALL_HIT,
        GOAL,
        VICTORY,
        POWERUP,
        COUNTDOWN
    }

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(GameConfig.Audio.MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()
    }

    private fun loadSounds() {
        soundPool?.let { pool ->
            // Загрузка звуковых файлов (при наличии в res/raw)
            // soundIds[SoundType.PADDLE_HIT] = pool.load(context, R.raw.hit_paddle, 1)
            // soundIds[SoundType.WALL_HIT] = pool.load(context, R.raw.hit_wall, 1)
            // soundIds[SoundType.GOAL] = pool.load(context, R.raw.goal, 1)
            // soundIds[SoundType.VICTORY] = pool.load(context, R.raw.victory, 1)
            // soundIds[SoundType.POWERUP] = pool.load(context, R.raw.powerup, 1)
            // soundIds[SoundType.COUNTDOWN] = pool.load(context, R.raw.countdown, 1)
        }
    }

    /**
     * Воспроизвести звуковой эффект
     */
    fun playSound(type: SoundType, customVolume: Float? = null) {
        if (!isEnabled) return

        soundPool?.let { pool ->
            soundIds[type]?.let { soundId ->
                val playVolume = customVolume ?: volume
                pool.play(soundId, playVolume, playVolume, 1, 0, 1f)
            }
        }
    }

    /**
     * Включить/выключить звуки
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /**
     * Установить громкость (0.0 - 1.0)
     */
    fun setVolume(newVolume: Float) {
        volume = newVolume.coerceIn(0f, 1f)
    }

    /**
     * Освободить ресурсы
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
}
