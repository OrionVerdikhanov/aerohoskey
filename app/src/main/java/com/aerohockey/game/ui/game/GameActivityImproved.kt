package com.aerohockey.game.ui.game

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.core.audio.SoundManager
import com.aerohockey.game.core.config.GameConfig
import com.aerohockey.game.core.haptic.VibrationManager
import com.aerohockey.game.data.repository.StatsRepository
import com.aerohockey.game.databinding.ActivityGameBinding
import com.aerohockey.game.domain.model.Difficulty
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.ui.result.ResultActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Улучшенный экран игры
 * Поддержка режимов, AI, power-ups, звуков, вибрации
 */
class GameActivityImproved : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var statsRepository: StatsRepository
    private lateinit var soundManager: SoundManager
    private lateinit var vibrationManager: VibrationManager

    private var gameMode: GameMode = GameMode.TwoPlayers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        initializeManagers()
        parseGameMode()
        setupGameView()
        setupCallbacks()
        setupBackPress()

        // Запуск игры
        binding.gameView.startGame(gameMode)
    }

    /**
     * Настройка окна
     */
    private fun setupWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()

        // Полноэкранный режим для современных версий
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }
    }

    /**
     * Инициализация менеджеров
     */
    private fun initializeManagers() {
        statsRepository = StatsRepository(this)
        soundManager = SoundManager(this)
        vibrationManager = VibrationManager(this)

        // Применение настроек из DataStore
        lifecycleScope.launch {
            statsRepository.statsFlow.collect { stats ->
                soundManager.setEnabled(stats.soundEnabled)
                vibrationManager.setEnabled(stats.vibrationEnabled)
            }
        }
    }

    /**
     * Парсинг режима игры из Intent
     */
    private fun parseGameMode() {
        val modeString = intent.getStringExtra(EXTRA_GAME_MODE) ?: "TWO_PLAYERS"
        gameMode = when {
            modeString == "TWO_PLAYERS" -> GameMode.TwoPlayers
            modeString.startsWith("VS_AI_") -> {
                val difficultyOrdinal = modeString.substringAfter("VS_AI_").toIntOrNull() ?: 1
                GameMode.VsAI(Difficulty.fromOrdinal(difficultyOrdinal))
            }
            modeString.startsWith("TOURNAMENT_") -> {
                val maxWins = modeString.substringAfter("TOURNAMENT_").toIntOrNull() ?: 7
                GameMode.Tournament(maxWins)
            }
            else -> GameMode.TwoPlayers
        }
    }

    /**
     * Настройка GameView
     */
    private fun setupGameView() {
        // Передаем менеджеры в GameView
        (binding.gameView as? GameViewImproved)?.apply {
            this.soundManager = this@GameActivityImproved.soundManager
            this.vibrationManager = this@GameActivityImproved.vibrationManager
        }
    }

    /**
     * Настройка колбэков игры
     */
    private fun setupCallbacks() {
        binding.gameView.onGoalScored = { playerId ->
            lifecycleScope.launch {
                delay(GameConfig.GOAL_CELEBRATION_DELAY)
                binding.gameView.resumeGame()
            }
        }

        binding.gameView.onGameFinished = { winnerId ->
            lifecycleScope.launch {
                // Сохранение результата
                statsRepository.saveGameResult(winnerId)

                // Звук победы
                soundManager.playSound(SoundManager.SoundType.VICTORY)
                vibrationManager.vibrate(VibrationManager.VibrationType.VICTORY)

                delay(1000)

                // Переход к результатам
                navigateToResults(winnerId)
            }
        }

        binding.gameView.onPowerUpCollected = { type ->
            soundManager.playSound(SoundManager.SoundType.POWERUP)
        }

        // Кнопка паузы
        binding.btnPause.setOnClickListener {
            showPauseDialog()
        }
    }

    /**
     * Современная обработка кнопки Назад
     */
    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showPauseDialog()
            }
        })
    }

    /**
     * Показать диалог паузы
     */
    private fun showPauseDialog() {
        binding.gameView.pauseGame()

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_pause_title)
            .setMessage(R.string.dialog_exit_message)
            .setPositiveButton(R.string.game_resume) { dialog, _ ->
                binding.gameView.resumeGame()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.game_exit) { _, _ ->
                finish()
            }
            .setNeutralButton(R.string.game_restart) { _, _ ->
                binding.gameView.startGame(gameMode)
            }
            .setCancelable(false)
            .show()
    }

    /**
     * Переход к экрану результатов
     */
    private fun navigateToResults(winnerId: Int) {
        val gameSession = binding.gameView.getGameSession()

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_WINNER_ID, winnerId)
            putExtra(ResultActivity.EXTRA_PLAYER1_SCORE, gameSession.player1Score)
            putExtra(ResultActivity.EXTRA_PLAYER2_SCORE, gameSession.player2Score)
        }
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        binding.gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        // Не возобновляем автоматически - пользователь должен сам продолжить
    }

    override fun onDestroy() {
        super.onDestroy()
        (binding.gameView as? GameViewImproved)?.release()
        soundManager.release()
    }

    companion object {
        const val EXTRA_GAME_MODE = "game_mode"
    }
}
