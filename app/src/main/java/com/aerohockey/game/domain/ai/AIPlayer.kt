package com.aerohockey.game.domain.ai

import com.aerohockey.game.domain.model.Difficulty
import com.aerohockey.game.domain.model.Paddle
import com.aerohockey.game.domain.model.Puck
import com.aerohockey.game.domain.model.Vector2D
import kotlin.math.abs
import kotlin.random.Random

/**
 * AI игрок с настраиваемой сложностью
 * Предсказывает траекторию шайбы и перемещает биту для защиты/атаки
 */
class AIPlayer(
    private val difficulty: Difficulty,
    private val fieldWidth: Float,
    private val fieldHeight: Float
) {
    private var lastUpdateTime = 0L
    private var targetPosition: Vector2D? = null
    private val centerX = fieldWidth / 2

    /**
     * Обновить позицию AI биты
     */
    fun updatePaddlePosition(
        paddle: Paddle,
        puck: Puck,
        deltaTime: Float
    ) {
        val currentTime = System.currentTimeMillis()

        // Проверка времени реакции
        if (currentTime - lastUpdateTime < difficulty.reactionTime * 1000) {
            return
        }

        lastUpdateTime = currentTime

        // Предсказание траектории шайбы
        val predictedPosition = predictPuckPosition(puck, paddle)

        // Добавление случайной ошибки в зависимости от сложности
        targetPosition = addErrorMargin(predictedPosition)

        // Движение к целевой позиции
        targetPosition?.let { target ->
            val dx = target.x - paddle.position.x
            val dy = target.y - paddle.position.y

            // Скорость движения AI
            val speed = paddle.maxSpeed * difficulty.speedMultiplier * deltaTime

            if (abs(dx) > 5f) {
                paddle.position.x += if (dx > 0) speed else -speed
            }
            if (abs(dy) > 5f) {
                paddle.position.y += if (dy > 0) speed else -speed
            }

            // Ограничения движения
            val centerY = fieldHeight / 2
            paddle.constrainToHalf(0f, fieldHeight, centerY)
            paddle.constrainHorizontally(0f, fieldWidth)
        }
    }

    /**
     * Предсказать позицию шайбы с учетом точности AI
     */
    private fun predictPuckPosition(puck: Puck, paddle: Paddle): Vector2D {
        // Базовое предсказание — текущая позиция шайбы
        val prediction = Vector2D(puck.position.x, puck.position.y)

        // Если шайба летит в сторону AI
        if ((paddle.playerId == 2 && puck.velocity.y < 0) ||
            (paddle.playerId == 1 && puck.velocity.y > 0)
        ) {
            // Предсказание с учетом скорости
            val predictionTime = difficulty.predictionAccuracy * 2f
            prediction.x = puck.position.x + puck.velocity.x * predictionTime
            prediction.y = puck.position.y + puck.velocity.y * predictionTime

            // Учет отскоков от стен
            if (prediction.x < 0) prediction.x = -prediction.x
            if (prediction.x > fieldWidth) prediction.x = fieldWidth - (prediction.x - fieldWidth)
        } else {
            // Шайба улетает — возвращаемся в центр
            prediction.x = centerX
            prediction.y = if (paddle.playerId == 2) fieldHeight * 0.25f else fieldHeight * 0.75f
        }

        return prediction
    }

    /**
     * Добавить случайную ошибку в позицию
     */
    private fun addErrorMargin(position: Vector2D): Vector2D {
        val errorX = Random.nextFloat() * difficulty.errorMargin - difficulty.errorMargin / 2
        val errorY = Random.nextFloat() * difficulty.errorMargin - difficulty.errorMargin / 2

        return Vector2D(
            position.x + errorX,
            position.y + errorY
        )
    }

    /**
     * Агрессивная атака (для более сложных уровней)
     */
    private fun shouldAttack(puck: Puck): Boolean {
        return when (difficulty) {
            Difficulty.EASY -> false
            Difficulty.MEDIUM -> Random.nextFloat() < 0.2f
            Difficulty.HARD -> Random.nextFloat() < 0.5f
            Difficulty.EXTREME -> Random.nextFloat() < 0.8f
        }
    }
}
