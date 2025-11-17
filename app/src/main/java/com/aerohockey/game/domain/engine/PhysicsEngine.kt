package com.aerohockey.game.domain.engine

import com.aerohockey.game.domain.model.Goal
import com.aerohockey.game.domain.model.Paddle
import com.aerohockey.game.domain.model.Puck
import com.aerohockey.game.domain.model.Vector2D
import kotlin.math.sqrt

/**
 * Физический движок для обработки столкновений и движения объектов
 */
class PhysicsEngine(
    private val fieldWidth: Float,
    private val fieldHeight: Float
) {
    companion object {
        private const val FRICTION = 0.98f // Коэффициент трения
        private const val RESTITUTION = 0.9f // Коэффициент упругости отскока
        private const val PADDLE_BOOST = 1.5f // Усиление скорости от биты
        private const val MAX_PUCK_SPEED = 1500f
    }

    /** Обновление физики игры */
    fun update(
        puck: Puck,
        paddle1: Paddle,
        paddle2: Paddle,
        goal1: Goal,
        goal2: Goal,
        deltaTime: Float
    ): Int? { // Возвращает ID игрока, забившего гол, или null
        // Обновление позиции шайбы
        puck.updatePosition(deltaTime)

        // Применение трения
        puck.applyFriction(FRICTION)

        // Проверка столкновений со стенами
        handleWallCollisions(puck)

        // Проверка столкновений с битами
        handlePaddleCollision(puck, paddle1)
        handlePaddleCollision(puck, paddle2)

        // Ограничение максимальной скорости
        puck.clampVelocity(MAX_PUCK_SPEED)

        // Проверка на гол
        return checkGoals(puck, goal1, goal2)
    }

    /** Обработка столкновений со стенами */
    private fun handleWallCollisions(puck: Puck) {
        val margin = puck.radius

        // Левая и правая стены
        if (puck.position.x - margin < 0) {
            puck.position.x = margin
            puck.velocity.x = -puck.velocity.x * RESTITUTION
        } else if (puck.position.x + margin > fieldWidth) {
            puck.position.x = fieldWidth - margin
            puck.velocity.x = -puck.velocity.x * RESTITUTION
        }

        // Верхняя и нижняя стены (без учета ворот - это проверяется отдельно)
        if (puck.position.y - margin < 0) {
            puck.position.y = margin
            puck.velocity.y = -puck.velocity.y * RESTITUTION
        } else if (puck.position.y + margin > fieldHeight) {
            puck.position.y = fieldHeight - margin
            puck.velocity.y = -puck.velocity.y * RESTITUTION
        }
    }

    /** Обработка столкновения шайбы с битой */
    private fun handlePaddleCollision(puck: Puck, paddle: Paddle) {
        val distance = puck.position.distanceTo(paddle.position)
        val minDistance = puck.radius + paddle.radius

        if (distance < minDistance) {
            // Нормализованный вектор от биты к шайбе
            val normal = (puck.position - paddle.position).normalize()

            // Разделение объектов
            val overlap = minDistance - distance
            puck.position.x += normal.x * overlap
            puck.position.y += normal.y * overlap

            // Относительная скорость
            val relativeVelocity = puck.velocity - paddle.velocity

            // Проекция относительной скорости на нормаль
            val velocityAlongNormal = relativeVelocity.dot(normal)

            // Не обрабатываем, если объекты уже расходятся
            if (velocityAlongNormal > 0) return

            // Импульс
            val impulseStrength = -(1 + RESTITUTION) * velocityAlongNormal
            val impulse = normal * impulseStrength

            // Применение импульса к шайбе с усилением от биты
            puck.applyImpulse(impulse * PADDLE_BOOST)

            // Добавление скорости биты к шайбе
            puck.applyImpulse(paddle.velocity * 0.3f)
        }
    }

    /** Проверка на забитый гол */
    private fun checkGoals(puck: Puck, goal1: Goal, goal2: Goal): Int? {
        return when {
            goal1.isPuckInside(puck) -> 2 // Игрок 2 забил в ворота игрока 1
            goal2.isPuckInside(puck) -> 1 // Игрок 1 забил в ворота игрока 2
            else -> null
        }
    }
}
