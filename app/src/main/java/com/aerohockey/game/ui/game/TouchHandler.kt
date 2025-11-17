package com.aerohockey.game.ui.game

import android.view.MotionEvent
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.domain.model.Paddle

/**
 * Обработчик мультитач-управления
 * Отделен от GameView для чистоты кода
 */
class TouchHandler(
    private val fieldWidth: Float,
    private val fieldHeight: Float
) {
    private val centerY = fieldHeight / 2f

    /**
     * Обработать касание экрана
     */
    fun handleTouch(
        event: MotionEvent,
        paddle1: Paddle,
        paddle2: Paddle,
        gameMode: GameMode
    ) {
        when (gameMode) {
            is GameMode.TwoPlayers -> handleTwoPlayerTouch(event, paddle1, paddle2)
            is GameMode.VsAI -> handleSinglePlayerTouch(event, paddle1)
            is GameMode.Tournament -> handleTwoPlayerTouch(event, paddle1, paddle2)
        }
    }

    /**
     * Обработка касаний для двух игроков
     */
    private fun handleTwoPlayerTouch(
        event: MotionEvent,
        paddle1: Paddle,
        paddle2: Paddle
    ) {
        for (i in 0 until event.pointerCount) {
            val x = event.getX(i)
            val y = event.getY(i)

            // Определение, какой игрок касается экрана
            if (y > centerY) {
                // Нижняя половина - игрок 1
                updatePaddlePosition(paddle1, x, y)
            } else {
                // Верхняя половина - игрок 2
                updatePaddlePosition(paddle2, x, y)
            }
        }
    }

    /**
     * Обработка касаний для одного игрока (vs AI)
     */
    private fun handleSinglePlayerTouch(
        event: MotionEvent,
        paddle1: Paddle
    ) {
        if (event.pointerCount > 0) {
            val x = event.getX(0)
            val y = event.getY(0)

            // Игрок 1 управляет только своей битой (нижняя половина)
            if (y > centerY) {
                updatePaddlePosition(paddle1, x, y)
            }
        }
    }

    /**
     * Обновить позицию биты с ограничениями
     */
    private fun updatePaddlePosition(paddle: Paddle, x: Float, y: Float) {
        paddle.setPosition(x, y)
        paddle.constrainToHalf(0f, fieldHeight, centerY)
        paddle.constrainHorizontally(0f, fieldWidth)
    }

    /**
     * Проверить, было ли касание в зоне игрока
     */
    fun isTouchInPlayerZone(y: Float, playerId: Int): Boolean {
        return when (playerId) {
            1 -> y > centerY
            2 -> y < centerY
            else -> false
        }
    }
}
