package com.aerohockey.game.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.aerohockey.game.domain.engine.PhysicsEngine
import com.aerohockey.game.domain.model.*

/**
 * Основной View для отображения и управления игрой
 */
class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null
    private lateinit var renderer: GameRenderer
    private lateinit var physicsEngine: PhysicsEngine

    // Игровые объекты
    private lateinit var puck: Puck
    private lateinit var paddle1: Paddle
    private lateinit var paddle2: Paddle
    private lateinit var goal1: Goal
    private lateinit var goal2: Goal

    // Состояние игры
    var gameSession = GameSession(maxScore = 7)
        private set

    // Колбэки
    var onGoalScored: ((playerId: Int) -> Unit)? = null
    var onGameFinished: ((winnerId: Int) -> Unit)? = null

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        initializeGame()
        startGameThread()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Реинициализация при изменении размера
        initializeGame()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGameThread()
    }

    /** Инициализация игровых объектов */
    private fun initializeGame() {
        val w = width.toFloat()
        val h = height.toFloat()

        // Инициализация рендерера и физики
        renderer = GameRenderer(w, h)
        physicsEngine = PhysicsEngine(w, h)

        // Создание шайбы
        puck = Puck(
            position = Vector2D(w / 2, h / 2),
            radius = 20f
        )

        // Создание бит
        paddle1 = Paddle(
            playerId = 1,
            position = Vector2D(w / 2, h * 0.75f),
            radius = 40f
        )

        paddle2 = Paddle(
            playerId = 2,
            position = Vector2D(w / 2, h * 0.25f),
            radius = 40f
        )

        // Создание ворот
        val goalWidth = w * 0.4f
        val goalHeight = 20f

        goal1 = Goal(playerId = 1).apply {
            setBounds(
                (w - goalWidth) / 2,
                h - goalHeight,
                (w + goalWidth) / 2,
                h
            )
        }

        goal2 = Goal(playerId = 2).apply {
            setBounds(
                (w - goalWidth) / 2,
                0f,
                (w + goalWidth) / 2,
                goalHeight
            )
        }
    }

    /** Запуск игрового потока */
    private fun startGameThread() {
        gameThread?.stopThread()
        gameThread = GameThread(holder).apply { start() }
    }

    /** Остановка игрового потока */
    private fun stopGameThread() {
        gameThread?.stopThread()
        gameThread = null
    }

    /** Запуск новой игры */
    fun startGame() {
        gameSession.reset()
        resetPuck()
        gameSession.state = GameState.Playing
    }

    /** Пауза игры */
    fun pauseGame() {
        if (gameSession.state is GameState.Playing) {
            gameSession.state = GameState.Paused
        }
    }

    /** Продолжение игры */
    fun resumeGame() {
        if (gameSession.state is GameState.Paused) {
            gameSession.state = GameState.Playing
        }
    }

    /** Сброс шайбы в центр */
    private fun resetPuck() {
        puck.reset(width / 2f, height / 2f)
    }

    /** Обработка касаний экрана */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameSession.state !is GameState.Playing) return true

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                handleTouch(event)
            }
            MotionEvent.ACTION_MOVE -> {
                handleTouch(event)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                // Можно обработать отпускание
            }
        }
        return true
    }

    /** Обработка мультитач для двух игроков */
    private fun handleTouch(event: MotionEvent) {
        val centerY = height / 2f

        for (i in 0 until event.pointerCount) {
            val x = event.getX(i)
            val y = event.getY(i)

            // Определение, какой игрок касается экрана
            if (y > centerY) {
                // Нижняя половина - игрок 1
                paddle1.setPosition(x, y)
                paddle1.constrainToHalf(0f, height.toFloat(), centerY)
                paddle1.constrainHorizontally(0f, width.toFloat())
            } else {
                // Верхняя половина - игрок 2
                paddle2.setPosition(x, y)
                paddle2.constrainToHalf(0f, height.toFloat(), centerY)
                paddle2.constrainHorizontally(0f, width.toFloat())
            }
        }
    }

    /** Игровой поток */
    private inner class GameThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        @Volatile
        private var running = false
        private val targetFPS = 60
        private val targetTime = 1000 / targetFPS

        init {
            running = true
        }

        fun stopThread() {
            running = false
            join()
        }

        override fun run() {
            var lastTime = System.currentTimeMillis()

            while (running) {
                val startTime = System.currentTimeMillis()
                val deltaTime = (startTime - lastTime) / 1000f
                lastTime = startTime

                update(deltaTime)
                render()

                val elapsed = System.currentTimeMillis() - startTime
                val sleepTime = targetTime - elapsed
                if (sleepTime > 0) {
                    sleep(sleepTime)
                }
            }
        }

        private fun update(deltaTime: Float) {
            if (gameSession.state is GameState.Playing) {
                val scoringPlayer = physicsEngine.update(
                    puck, paddle1, paddle2, goal1, goal2, deltaTime
                )

                scoringPlayer?.let {
                    gameSession.addScore(it)
                    onGoalScored?.invoke(it)

                    when (gameSession.state) {
                        is GameState.Finished -> {
                            onGameFinished?.invoke((gameSession.state as GameState.Finished).winnerId)
                        }
                        else -> {
                            // Сброс шайбы после гола
                            postDelayed({ resetPuck() }, 1000)
                        }
                    }
                }
            }
        }

        private fun render() {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                canvas?.let {
                    synchronized(surfaceHolder) {
                        renderer.drawField(it)
                        renderer.drawGoals(it, goal1, goal2)
                        renderer.drawPuck(it, puck)
                        renderer.drawPaddles(it, paddle1, paddle2)
                        renderer.drawScore(it, gameSession.player1Score, gameSession.player2Score)

                        when (val state = gameSession.state) {
                            is GameState.Countdown -> renderer.drawCountdown(it, state.seconds)
                            is GameState.GoalScored -> renderer.drawGoalMessage(it, state.scoringPlayer)
                            else -> {}
                        }
                    }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
        }
    }
}
