package com.aerohockey.game.ui.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.aerohockey.game.domain.model.Goal
import com.aerohockey.game.domain.model.Paddle
import com.aerohockey.game.domain.model.Puck

/**
 * Отрисовщик элементов игры
 */
class GameRenderer(
    private val fieldWidth: Float,
    private val fieldHeight: Float
) {
    // Краски для отрисовки
    private val fieldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A472A") // Темно-зеленый цвет поля
    }

    private val centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val puckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFD700") // Золотой цвет шайбы
    }

    private val puckShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
    }

    private val paddle1Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4444") // Красный игрок 1
    }

    private val paddle2Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4444FF") // Синий игрок 2
    }

    private val goalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
    }

    /** Отрисовка игрового поля */
    fun drawField(canvas: Canvas) {
        // Фон поля
        canvas.drawRect(0f, 0f, fieldWidth, fieldHeight, fieldPaint)

        // Центральная линия
        val centerY = fieldHeight / 2
        canvas.drawLine(0f, centerY, fieldWidth, centerY, centerLinePaint)

        // Центральный круг
        val centerX = fieldWidth / 2
        canvas.drawCircle(centerX, centerY, 100f, centerCirclePaint)
    }

    /** Отрисовка ворот */
    fun drawGoals(canvas: Canvas, goal1: Goal, goal2: Goal) {
        // Ворота игрока 1 (внизу)
        canvas.drawRect(goal1.bounds, goalPaint)

        // Ворота игрока 2 (вверху)
        canvas.drawRect(goal2.bounds, goalPaint)
    }

    /** Отрисовка шайбы */
    fun drawPuck(canvas: Canvas, puck: Puck) {
        // Тень
        canvas.drawCircle(
            puck.position.x + 3f,
            puck.position.y + 3f,
            puck.radius,
            puckShadowPaint
        )

        // Шайба
        canvas.drawCircle(
            puck.position.x,
            puck.position.y,
            puck.radius,
            puckPaint
        )
    }

    /** Отрисовка бит */
    fun drawPaddles(canvas: Canvas, paddle1: Paddle, paddle2: Paddle) {
        // Бита игрока 1
        canvas.drawCircle(
            paddle1.position.x,
            paddle1.position.y,
            paddle1.radius,
            paddle1Paint
        )

        // Бита игрока 2
        canvas.drawCircle(
            paddle2.position.x,
            paddle2.position.y,
            paddle2.radius,
            paddle2Paint
        )
    }

    /** Отрисовка счета */
    fun drawScore(canvas: Canvas, player1Score: Int, player2Score: Int) {
        val centerX = fieldWidth / 2

        // Счет игрока 2 (вверху)
        canvas.save()
        canvas.rotate(180f, centerX, fieldHeight * 0.2f)
        canvas.drawText(
            player2Score.toString(),
            centerX,
            fieldHeight * 0.2f,
            scorePaint
        )
        canvas.restore()

        // Счет игрока 1 (внизу)
        canvas.drawText(
            player1Score.toString(),
            centerX,
            fieldHeight * 0.8f,
            scorePaint
        )
    }

    /** Отрисовка обратного отсчета */
    fun drawCountdown(canvas: Canvas, seconds: Int) {
        val countdownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            seconds.toString(),
            fieldWidth / 2,
            fieldHeight / 2 + 40f,
            countdownPaint
        )
    }

    /** Отрисовка сообщения о голе */
    fun drawGoalMessage(canvas: Canvas, scoringPlayer: Int) {
        val messagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (scoringPlayer == 1) Color.parseColor("#FF4444") else Color.parseColor("#4444FF")
            textSize = 80f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            "ГОЛ!",
            fieldWidth / 2,
            fieldHeight / 2,
            messagePaint
        )
    }
}
