package com.aerohockey.game.domain.model

import com.aerohockey.game.core.config.GameConfig
import kotlin.random.Random

/**
 * Бонус, появляющийся на поле
 */
data class PowerUp(
    val type: PowerUpType,
    val position: Vector2D,
    val radius: Float = 25f,
    var isActive: Boolean = true,
    val spawnTime: Long = System.currentTimeMillis()
) {
    /** Проверка, собрал ли игрок бонус */
    fun isCollectedBy(paddle: Paddle): Boolean {
        if (!isActive) return false
        val distance = position.distanceTo(paddle.position)
        return distance < (radius + paddle.radius)
    }

    /** Применить эффект бонуса */
    fun apply(paddle: Paddle, puck: Puck) {
        when (type) {
            PowerUpType.SPEED_BOOST -> {
                puck.velocity.x *= GameConfig.PowerUps.SPEED_MULTIPLIER
                puck.velocity.y *= GameConfig.PowerUps.SPEED_MULTIPLIER
            }
            PowerUpType.BIG_PADDLE -> {
                // Эффект применяется через внешний таймер
            }
            PowerUpType.SLOW_MOTION -> {
                puck.velocity.x *= 0.5f
                puck.velocity.y *= 0.5f
            }
            PowerUpType.FREEZE_OPPONENT -> {
                // Эффект применяется к противнику через внешний механизм
            }
        }
        isActive = false
    }

    companion object {
        /** Создать случайный бонус */
        fun createRandom(fieldWidth: Float, fieldHeight: Float): PowerUp {
            val type = PowerUpType.values().random()
            val x = Random.nextFloat() * fieldWidth * 0.6f + fieldWidth * 0.2f
            val y = Random.nextFloat() * fieldHeight * 0.6f + fieldHeight * 0.2f

            return PowerUp(
                type = type,
                position = Vector2D(x, y)
            )
        }
    }
}

/**
 * Типы бонусов
 */
enum class PowerUpType(val color: Int, val icon: String) {
    SPEED_BOOST(0xFFFF4444.toInt(), "⚡"),      // Ускорение шайбы
    BIG_PADDLE(0xFF44FF44.toInt(), "⬆"),       // Увеличение биты
    SLOW_MOTION(0xFF4444FF.toInt(), "🐢"),     // Замедление шайбы
    FREEZE_OPPONENT(0xFFFFFF00.toInt(), "❄")   // Заморозка противника
}

/**
 * Активный эффект от бонуса
 */
data class ActivePowerUpEffect(
    val type: PowerUpType,
    val playerId: Int,
    val startTime: Long = System.currentTimeMillis(),
    val duration: Long = GameConfig.PowerUps.DURATION
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - startTime > duration
    }

    fun getRemainingTime(): Long {
        return duration - (System.currentTimeMillis() - startTime)
    }
}
