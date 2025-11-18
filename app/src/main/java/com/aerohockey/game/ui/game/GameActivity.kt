package com.aerohockey.game.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.core.audio.SoundManager
import com.aerohockey.game.core.haptic.VibrationManager
import com.aerohockey.game.data.repository.StatsRepository
import com.aerohockey.game.databinding.ActivityGameBinding
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.ui.result.ResultActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран игры
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var statsRepository: StatsRepository
    private lateinit var soundManager: SoundManager
    private lateinit var vibrationManager: VibrationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Полноэкранный режим
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()

        statsRepository = StatsRepository(this)
        soundManager = SoundManager(this)
        vibrationManager = VibrationManager(this)

        // Подключаем менеджеры к GameView
        binding.gameView.soundManager = soundManager
        binding.gameView.vibrationManager = vibrationManager

        setupGameCallbacks()
        setupPauseButton()

        // Запуск игры (по умолчанию режим 2 игрока)
        val gameMode = intent.getSerializableExtra(EXTRA_GAME_MODE) as? GameMode ?: GameMode.TwoPlayers
        binding.gameView.startGame(gameMode)
    }

    private fun setupGameCallbacks() {
        // Обработка забитого гола
        binding.gameView.onGoalScored = { playerId ->
            // Можно добавить звуковые эффекты или вибрацию
            lifecycleScope.launch {
                delay(2000) // Пауза после гола
                binding.gameView.resumeGame()
            }
        }

        // Обработка окончания игры
        binding.gameView.onGameFinished = { winnerId ->
            lifecycleScope.launch {
                // Сохранение результата
                statsRepository.saveGameResult(winnerId)

                // Определяем perfect game (победа без пропущенных голов)
                val isPerfectGame = when (winnerId) {
                    1 -> binding.gameView.gameSession.player2Score == 0
                    2 -> binding.gameView.gameSession.player1Score == 0
                    else -> false
                }

                // Переход к экрану результатов
                val intent = Intent(this@GameActivity, ResultActivity::class.java).apply {
                    putExtra(ResultActivity.EXTRA_WINNER_ID, winnerId)
                    putExtra(
                        ResultActivity.EXTRA_PLAYER1_SCORE,
                        binding.gameView.gameSession.player1Score
                    )
                    putExtra(
                        ResultActivity.EXTRA_PLAYER2_SCORE,
                        binding.gameView.gameSession.player2Score
                    )
                    putExtra(
                        ResultActivity.EXTRA_MAX_COMBO,
                        binding.gameView.comboSystem.maxCombo
                    )
                    putExtra(
                        ResultActivity.EXTRA_PERFECT_GAME,
                        isPerfectGame
                    )
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupPauseButton() {
        binding.btnPause.setOnClickListener {
            showPauseDialog()
        }
    }

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
                val gameMode = intent.getSerializableExtra(EXTRA_GAME_MODE) as? GameMode ?: GameMode.TwoPlayers
                binding.gameView.startGame(gameMode)
            }
            .setCancelable(false)
            .show()
    }

    override fun onPause() {
        super.onPause()
        binding.gameView.pauseGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }

    override fun onBackPressed() {
        showPauseDialog()
    }

    companion object {
        const val EXTRA_GAME_MODE = "game_mode"
    }
}
