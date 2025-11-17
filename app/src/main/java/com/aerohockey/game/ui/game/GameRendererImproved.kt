package com.aerohockey.game.ui.game

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.aerohockey.game.R
import com.aerohockey.game.domain.model.*

/**
 * Улучшенный рендерер игры
 * Использует ресурсы вместо хардкода, поддерживает power-ups и эффекты
 */
class GameRendererImproved(
    private val context: Context,
    private val fieldWidth: Float,
    private val fieldHeight: Float
) {
    // Краски для элементов поля
    private val fieldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_field)
    }

    private val centerLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    // Краски для игровых объектов
    private val puckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_puck)
    }

    private val puckTrailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.game_puck_trail)
        alpha = 100
    }

    private val puckShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40000000")
    }

    private val paddle1Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.player1_color)
    }

    private val paddle2Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.player2_color)
    }

    private val goalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 60f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val powerUpPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val powerUpIconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    // Trail эффект для шайбы
    private val puckTrail = mutableListOf<Vector2D>()
    private val maxTrailLength = 10

    /**
     * Основной метод отрисовки
     */
    fun render(canvas: Canvas, controller: GameController) {
        drawField(canvas)
        drawGoals(canvas, controller.goal1, controller.goal2)
        drawPuckTrail(canvas)
        drawPuck(canvas, controller.puck)
        drawPaddles(canvas, controller.paddle1, controller.paddle2)
        drawPowerUps(canvas, controller.powerUps)
        drawScore(canvas, controller.gameSession)
        drawActivePowerUpEffects(canvas, controller.activePowerUpEffects)

        when (val state = controller.gameSession.state) {
            is GameState.Countdown -> drawCountdown(canvas, state.seconds)
            is GameState.GoalScored -> drawGoalMessage(canvas, state.scoringPlayer)
            else -> {}
        }
    }

    /** Отрисовка поля */
    fun drawField(canvas: Canvas) {
        canvas.drawRect(0f, 0f, fieldWidth, fieldHeight, fieldPaint)

        val centerY = fieldHeight / 2
        canvas.drawLine(0f, centerY, fieldWidth, centerY, centerLinePaint)

        val centerX = fieldWidth / 2
        canvas.drawCircle(centerX, centerY, 100f, centerCirclePaint)
    }

    /** Отрисовка ворот */
    fun drawGoals(canvas: Canvas, goal1: Goal, goal2: Goal) {
        canvas.drawRect(goal1.bounds, goalPaint)
        canvas.drawRect(goal2.bounds, goalPaint)
    }

    /** Отрисовка trail эффекта */
    private fun drawPuckTrail(canvas: Canvas) {
        puckTrail.forEachIndexed { index, pos ->
            val alpha = ((index.toFloat() / maxTrailLength) * 100).toInt()
            puckTrailPaint.alpha = alpha
            canvas.drawCircle(pos.x, pos.y, 15f, puckTrailPaint)
        }
    }

    /** Отрисовка шайбы с trail */
    fun drawPuck(canvas: Canvas, puck: Puck) {
        // Добавление в trail
        puckTrail.add(Vector2D(puck.position.x, puck.position.y))
        if (puckTrail.size > maxTrailLength) {
            puckTrail.removeAt(0)
        }

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
        canvas.drawCircle(
            paddle1.position.x,
            paddle1.position.y,
            paddle1.radius,
            paddle1Paint
        )

        canvas.drawCircle(
            paddle2.position.x,
            paddle2.position.y,
            paddle2.radius,
            paddle2Paint
        )
    }

    /** Отрисовка power-ups */
    private fun drawPowerUps(canvas: Canvas, powerUps: List<PowerUp>) {
        powerUps.forEach { powerUp ->
            if (powerUp.isActive) {
                powerUpPaint.color = powerUp.type.color

                // Круг бонуса
                canvas.drawCircle(
                    powerUp.position.x,
                    powerUp.position.y,
                    powerUp.radius,
                    powerUpPaint
                )

                // Иконка
                canvas.drawText(
                    powerUp.type.icon,
                    powerUp.position.x,
                    powerUp.position.y + 10f,
                    powerUpIconPaint
                )
            }
        }
    }

    /** Отрисовка счета */
    fun drawScore(canvas: Canvas, session: GameSession) {
        val centerX = fieldWidth / 2

        // Счет игрока 2 (вверху, перевернут)
        canvas.save()
        canvas.rotate(180f, centerX, fieldHeight * 0.2f)
        canvas.drawText(
            session.player2Score.toString(),
            centerX,
            fieldHeight * 0.2f,
            scorePaint
        )
        canvas.restore()

        // Счет игрока 1 (внизу)
        canvas.drawText(
            session.player1Score.toString(),
            centerX,
            fieldHeight * 0.8f,
            scorePaint
        )
    }

    /** Отрисовка активных эффектов */
    private fun drawActivePowerUpEffects(
        canvas: Canvas,
        effects: List<ActivePowerUpEffect>
    ) {
        effects.forEachIndexed { index, effect ->
            val y = 50f + index * 40f
            val text = "${effect.type.icon} ${effect.getRemainingTime() / 1000}s"

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (effect.playerId == 1) {
                    ContextCompat.getColor(context, R.color.player1_color)
                } else {
                    ContextCompat.getColor(context, R.color.player2_color)
                }
                textSize = 24f
            }

            canvas.drawText(text, 20f, y, paint)
        }
    }

    /** Отрисовка обратного отсчета */
    fun drawCountdown(canvas: Canvas, seconds: Int) {
        val countdownPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
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
            color = if (scoringPlayer == 1) {
                ContextCompat.getColor(context, R.color.player1_color)
            } else {
                ContextCompat.getColor(context, R.color.player2_color)
            }
            textSize = 80f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText(
            context.getString(R.string.game_goal),
            fieldWidth / 2,
            fieldHeight / 2,
            messagePaint
        )
    }
}
