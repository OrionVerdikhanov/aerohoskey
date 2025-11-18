package com.aerohockey.game.ui.game

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.aerohockey.game.core.audio.SoundManager
import com.aerohockey.game.core.haptic.VibrationManager
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.domain.model.GameState
import com.aerohockey.game.domain.model.PowerUpType
import com.aerohockey.game.domain.progression.Skin
import com.aerohockey.game.domain.progression.SkinType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    // Репозиторий прогресса для загрузки скинов
    private lateinit var progressRepository: PlayerProgressRepository
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Колбэки для Activity
    var onGoalScored: ((playerId: Int) -> Unit)? = null
    var onGameFinished: ((winnerId: Int) -> Unit)? = null
    var onPowerUpCollected: ((type: PowerUpType) -> Unit)? = null
    var onComboTriggered: ((combo: Int, message: String?, multiplier: Float) -> Unit)? = null

    init {
        holder.addCallback(this)
        isFocusable = true
        progressRepository = PlayerProgressRepository(context)
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
            onComboTriggered = this@GameViewImproved.onComboTriggered
        }

        // Загружаем и применяем скины
        loadAndApplySkins()
    }

    /**
     * Загрузка и применение экипированных скинов
     */
    private fun loadAndApplySkins() {
        coroutineScope.launch {
            try {
                // Получаем ID экипированных скинов
                val equippedPuckId = progressRepository.getEquippedSkin(SkinType.PUCK).first()
                val equippedPaddleId = progressRepository.getEquippedSkin(SkinType.PADDLE).first()

                // Находим скины по ID
                val puckSkin = Skin.ALL_SKINS.find { it.id == equippedPuckId }
                val paddleSkin = Skin.ALL_SKINS.find { it.id == equippedPaddleId }

                // Применяем цвета к рендереру
                puckSkin?.let { renderer.setPuckColor(it.color) }
                paddleSkin?.let {
                    renderer.setPaddle1Color(it.color)
                    renderer.setPaddle2Color(it.color)
                }
            } catch (e: Exception) {
                // Игнорируем ошибки загрузки скинов, используем цвета по умолчанию
            }
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
