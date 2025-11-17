package com.aerohockey.game.core.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.aerohockey.game.core.config.GameConfig

/**
 * Менеджер вибрации
 * Управляет тактильной обратной связью во время игры
 */
class VibrationManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var isEnabled = true

    enum class VibrationType {
        PADDLE_HIT,
        WALL_HIT,
        GOAL,
        VICTORY
    }

    /**
     * Выполнить вибрацию
     */
    fun vibrate(type: VibrationType) {
        if (!isEnabled || vibrator?.hasVibrator() != true) return

        val duration = when (type) {
            VibrationType.PADDLE_HIT -> GameConfig.Haptic.PADDLE_HIT_DURATION
            VibrationType.WALL_HIT -> GameConfig.Haptic.WALL_HIT_DURATION
            VibrationType.GOAL -> GameConfig.Haptic.GOAL_DURATION
            VibrationType.VICTORY -> GameConfig.Haptic.GOAL_DURATION
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (type) {
                VibrationType.PADDLE_HIT ->
                    VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                VibrationType.WALL_HIT ->
                    VibrationEffect.createOneShot(duration, (VibrationEffect.DEFAULT_AMPLITUDE * 0.7f).toInt())
                VibrationType.GOAL ->
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 100, 50, 100),
                        intArrayOf(0, 255, 0, 255),
                        -1
                    )
                VibrationType.VICTORY ->
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 100, 50, 100, 50, 200),
                        intArrayOf(0, 255, 0, 255, 0, 255),
                        -1
                    )
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    /**
     * Включить/выключить вибрацию
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /**
     * Проверить доступность вибрации
     */
    fun isAvailable(): Boolean {
        return vibrator?.hasVibrator() == true
    }
}
