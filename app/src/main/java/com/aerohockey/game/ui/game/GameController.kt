package com.aerohockey.game.ui.game

import com.aerohockey.game.core.audio.SoundManager
import com.aerohockey.game.core.config.GameConfig
import com.aerohockey.game.core.haptic.VibrationManager
import com.aerohockey.game.domain.ai.AIPlayer
import com.aerohockey.game.domain.engine.PhysicsEngine
import com.aerohockey.game.domain.model.*
import com.aerohockey.game.domain.progression.ComboSystem

/**
 * Контроллер игры — управляет состоянием и логикой
 * Отделен от View для чистоты архитектуры
 */
class GameController(
    private val fieldWidth: Float,
    private val fieldHeight: Float,
    private val soundManager: SoundManager?,
    private val vibrationManager: VibrationManager?
) {
    // Физика и AI
    private val physicsEngine = PhysicsEngine(fieldWidth, fieldHeight)
    private var aiPlayer: AIPlayer? = null

    // Игровые объекты
    lateinit var puck: Puck
    lateinit var paddle1: Paddle
    lateinit var paddle2: Paddle
    lateinit var goal1: Goal
    lateinit var goal2: Goal

    // Состояние
    var gameSession = GameSession(maxScore = GameConfig.DEFAULT_MAX_SCORE)
    var gameMode: GameMode = GameMode.TwoPlayers
    var powerUps = mutableListOf<PowerUp>()
    var activePowerUpEffects = mutableListOf<ActivePowerUpEffect>()
    var comboSystem = ComboSystem()

    // Колбэки
    var onGoalScored: ((playerId: Int) -> Unit)? = null
    var onGameFinished: ((winnerId: Int) -> Unit)? = null
    var onPowerUpCollected: ((type: PowerUpType) -> Unit)? = null
    var onComboTriggered: ((combo: Int, message: String?, multiplier: Float) -> Unit)? = null

    // Таймеры
    private var lastPowerUpSpawnTime = 0L

    /**
     * Инициализация игровых объектов
     */
    fun initialize(mode: GameMode) {
        this.gameMode = mode

        // Создание шайбы
        puck = Puck(
            position = Vector2D(fieldWidth / 2, fieldHeight / 2),
            radius = fieldWidth * GameConfig.PUCK_RADIUS_RATIO
        )

        // Создание бит
        val paddleRadius = fieldWidth * GameConfig.PADDLE_RADIUS_RATIO
        paddle1 = Paddle(
            playerId = 1,
            position = Vector2D(fieldWidth / 2, fieldHeight * 0.75f),
            radius = paddleRadius
        )

        paddle2 = Paddle(
            playerId = 2,
            position = Vector2D(fieldWidth / 2, fieldHeight * 0.25f),
            radius = paddleRadius
        )

        // Создание ворот
        val goalWidth = fieldWidth * GameConfig.GOAL_WIDTH_RATIO
        val goalHeight = fieldHeight * GameConfig.GOAL_HEIGHT_RATIO

        goal1 = Goal(playerId = 1).apply {
            setBounds(
                (fieldWidth - goalWidth) / 2,
                fieldHeight - goalHeight,
                (fieldWidth + goalWidth) / 2,
                fieldHeight
            )
        }

        goal2 = Goal(playerId = 2).apply {
            setBounds(
                (fieldWidth - goalWidth) / 2,
                0f,
                (fieldWidth + goalWidth) / 2,
                goalHeight
            )
        }

        // Инициализация AI если нужно
        if (mode is GameMode.VsAI) {
            aiPlayer = AIPlayer(mode.difficulty, fieldWidth, fieldHeight)
            AIPlayer.setPaddle(paddle2)
        }
    }

    /**
     * Обновление игровой логики
     */
    fun update(deltaTime: Float) {
        when (gameSession.state) {
            is GameState.Playing -> updatePlaying(deltaTime)
            is GameState.GoalScored -> updateGoalState()
            else -> {}
        }
    }

    private fun updatePlaying(deltaTime: Float) {
        // Обновление AI
        aiPlayer?.updatePaddlePosition(paddle2, puck, deltaTime)

        // Обновление power-ups эффектов
        updatePowerUpEffects()

        // Обновление физики
        val scoringPlayer = physicsEngine.update(
            puck, paddle1, paddle2, goal1, goal2, deltaTime
        )

        // Проверка голов
        scoringPlayer?.let { handleGoal(it) }

        // Спавн power-ups
        spawnPowerUps()

        // Проверка сбора power-ups
        checkPowerUpCollection()
    }

    private fun updateGoalState() {
        // Ожидаем анимацию гола
    }

    /**
     * Обработка забитого гола
     */
    private fun handleGoal(scoringPlayer: Int) {
        soundManager?.playSound(SoundManager.SoundType.GOAL)
        vibrationManager?.vibrate(VibrationManager.VibrationType.GOAL)

        // Обработка комбо
        val comboResult = comboSystem.addGoal(scoringPlayer)
        if (comboResult.combo >= 2) {
            onComboTriggered?.invoke(
                comboResult.combo,
                comboResult.message,
                comboResult.multiplier
            )
        }

        gameSession.addScore(scoringPlayer)
        onGoalScored?.invoke(scoringPlayer)

        when (gameSession.state) {
            is GameState.Finished -> {
                onGameFinished?.invoke((gameSession.state as GameState.Finished).winnerId)
            }
            else -> {
                // Сброс шайбы произойдет через delay в Activity
            }
        }
    }

    /**
     * Спавн power-ups
     */
    private fun spawnPowerUps() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPowerUpSpawnTime > GameConfig.PowerUps.SPAWN_INTERVAL) {
            powerUps.add(PowerUp.createRandom(fieldWidth, fieldHeight))
            lastPowerUpSpawnTime = currentTime
            soundManager?.playSound(SoundManager.SoundType.POWERUP)
        }
    }

    /**
     * Проверка сбора power-ups
     */
    private fun checkPowerUpCollection() {
        powerUps.forEach { powerUp ->
            if (powerUp.isActive) {
                when {
                    powerUp.isCollectedBy(paddle1) -> {
                        powerUp.apply(paddle1, puck)
                        activePowerUpEffects.add(
                            ActivePowerUpEffect(powerUp.type, 1)
                        )
                        onPowerUpCollected?.invoke(powerUp.type)
                    }
                    powerUp.isCollectedBy(paddle2) -> {
                        powerUp.apply(paddle2, puck)
                        activePowerUpEffects.add(
                            ActivePowerUpEffect(powerUp.type, 2)
                        )
                        onPowerUpCollected?.invoke(powerUp.type)
                    }
                }
            }
        }

        // Удаление неактивных power-ups
        powerUps.removeAll { !it.isActive }
    }

    /**
     * Обновление активных эффектов
     */
    private fun updatePowerUpEffects() {
        activePowerUpEffects.removeAll { it.isExpired() }
    }

    /**
     * Запуск игры
     */
    fun startGame() {
        gameSession.reset()
        resetPuck()
        powerUps.clear()
        activePowerUpEffects.clear()
        comboSystem.reset()
        gameSession.state = GameState.Playing
        lastPowerUpSpawnTime = System.currentTimeMillis()
    }

    /**
     * Сброс шайбы в центр
     */
    fun resetPuck() {
        puck.reset(fieldWidth / 2f, fieldHeight / 2f)
    }

    /**
     * Пауза
     */
    fun pauseGame() {
        if (gameSession.state is GameState.Playing) {
            gameSession.state = GameState.Paused
        }
    }

    /**
     * Продолжение
     */
    fun resumeGame() {
        if (gameSession.state is GameState.Paused) {
            gameSession.state = GameState.Playing
        }
    }
}
