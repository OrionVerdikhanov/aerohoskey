package com.aerohockey.game.domain.model

/**
 * Модель биты игрока
 */
data class Paddle(
    val playerId: Int, // 1 или 2
    val position: Vector2D = Vector2D(),
    val velocity: Vector2D = Vector2D(),
    val radius: Float = 40f,
    val maxSpeed: Float = 800f
) {
    private val previousPosition = Vector2D()

    /** Установка позиции биты */
    fun setPosition(x: Float, y: Float) {
        previousPosition.set(position.x, position.y)
        position.set(x, y)

        // Вычисление скорости по изменению позиции
        velocity.set(
            position.x - previousPosition.x,
            position.y - previousPosition.y
        )
    }

    /** Ограничение движения биты в своей половине поля */
    fun constrainToHalf(fieldTop: Float, fieldBottom: Float, centerY: Float) {
        if (playerId == 1) {
            // Нижний игрок - может двигаться только в нижней половине
            if (position.y < centerY) {
                position.y = centerY
            }
            if (position.y > fieldBottom - radius) {
                position.y = fieldBottom - radius
            }
        } else {
            // Верхний игрок - может двигаться только в верхней половине
            if (position.y > centerY) {
                position.y = centerY
            }
            if (position.y < fieldTop + radius) {
                position.y = fieldTop + radius
            }
        }
    }

    /** Ограничение по горизонтали */
    fun constrainHorizontally(fieldLeft: Float, fieldRight: Float) {
        if (position.x < fieldLeft + radius) {
            position.x = fieldLeft + radius
        }
        if (position.x > fieldRight - radius) {
            position.x = fieldRight - radius
        }
    }
}
