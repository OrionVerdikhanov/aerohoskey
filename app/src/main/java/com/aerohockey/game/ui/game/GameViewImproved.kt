package com.aerohockey.game.ui.game

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.aerohockey.game.core.audio.SoundManager
import com.aerohockey.game.core.haptic.VibrationManager
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.domain.model.GameState
import com.aerohockey.game.domain.model.PowerUpType

/**
 * Улучшенный GameView — только отображение и обработка касаний
 * Вся логика вынесена в GameController
 */
class GameViewImproved @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    // Компоненты
    private var gameLoopThread: GameLoopThread? = null
    private lateinit var gameController: GameController
    private lateinit var renderer: GameRendererImproved
    private lateinit var touchHandler: TouchHandler

    // Менеджеры
    var soundManager: SoundManager? = null
    var vibrationManager: VibrationManager? = null

    // Колбэки для Activity
    var onGoalScored: ((playerId: Int) -> Unit)? = null
    var onGameFinished: ((winnerId: Int) -> Unit)? = null
    var onPowerUpCollected: ((type: PowerUpType) -> Unit)? = null

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        initialize()
        startGameLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Реинициализация при изменении размера
        if (width > 0 && height > 0) {
            initialize()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGameLoop()
    }

    /**
     * Инициализация компонентов
     */
    private fun initialize() {
        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        renderer = GameRendererImproved(context, w, h)
        touchHandler = TouchHandler(w, h)

        gameController = GameController(w, h, soundManager, vibrationManager).apply {
            onGoalScored = this@GameViewImproved.onGoalScored
            onGameFinished = this@GameViewImproved.onGameFinished
            onPowerUpCollected = this@GameViewImproved.onPowerUpCollected
        }
    }

    /**
     * Запуск игрового цикла
     */
    private fun startGameLoop() {
        stopGameLoop()
        gameLoopThread = GameLoopThread(holder, gameController, renderer).apply {
            startLoop()
        }
    }

    /**
     * Остановка игрового цикла
     */
    private fun stopGameLoop() {
        gameLoopThread?.stopLoop()
        gameLoopThread = null
    }

    /**
     * Запуск новой игры
     */
    fun startGame(mode: GameMode) {
        gameController.initialize(mode)
        gameController.startGame()
    }

    /**
     * Пауза
     */
    fun pauseGame() {
        gameController.pauseGame()
    }

    /**
     * Продолжение
     */
    fun resumeGame() {
        gameController.resumeGame()
    }

    /**
     * Сброс шайбы
     */
    fun resetPuck() {
        gameController.resetPuck()
    }

    /**
     * Получить текущую сессию
     */
    fun getGameSession() = gameController.gameSession

    /**
     * Получить систему комбо
     */
    val comboSystem get() = gameController.comboSystem

    /**
     * Получить сессию (для удобства доступа)
     */
    val gameSession get() = gameController.gameSession

    /**
     * Обработка касаний
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameController.gameSession.state !is GameState.Playing) {
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_MOVE -> {
                touchHandler.handleTouch(
                    event,
                    gameController.paddle1,
                    gameController.paddle2,
                    gameController.gameMode
                )
            }
        }

        return true
    }

    /**
     * Освобождение ресурсов
     */
    fun release() {
        stopGameLoop()
        soundManager?.release()
    }
}
