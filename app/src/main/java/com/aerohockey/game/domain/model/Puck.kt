package com.aerohockey.game.domain.model

/**
 * Модель шайбы для игры в аэрохоккей
 */
data class Puck(
    val position: Vector2D = Vector2D(),
    val velocity: Vector2D = Vector2D(),
    val radius: Float = 20f
) {
    /** Сброс шайбы в центр поля */
    fun reset(centerX: Float, centerY: Float) {
        position.set(centerX, centerY)
        velocity.set(0f, 0f)
    }

    /** Применение импульса к шайбе */
    fun applyImpulse(impulse: Vector2D) {
        velocity.x += impulse.x
        velocity.y += impulse.y
    }

    /** Ограничение максимальной скорости */
    fun clampVelocity(maxSpeed: Float) {
        val speed = velocity.length()
        if (speed > maxSpeed) {
            velocity.x = (velocity.x / speed) * maxSpeed
            velocity.y = (velocity.y / speed) * maxSpeed
        }
    }

    /** Применение трения */
    fun applyFriction(friction: Float) {
        velocity.x *= friction
        velocity.y *= friction
    }

    /** Обновление позиции */
    fun updatePosition(deltaTime: Float) {
        position.x += velocity.x * deltaTime
        position.y += velocity.y * deltaTime
    }
}
