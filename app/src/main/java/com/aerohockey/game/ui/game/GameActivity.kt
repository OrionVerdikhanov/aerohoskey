package com.aerohockey.game.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.StatsRepository
import com.aerohockey.game.databinding.ActivityGameBinding
import com.aerohockey.game.ui.result.ResultActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Экран игры
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var statsRepository: StatsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Полноэкранный режим
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()

        statsRepository = StatsRepository(this)

        setupGameCallbacks()
        setupPauseButton()

        // Запуск игры
        binding.gameView.startGame()
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
                binding.gameView.startGame()
            }
            .setCancelable(false)
            .show()
    }

    override fun onPause() {
        super.onPause()
        binding.gameView.pauseGame()
    }

    override fun onBackPressed() {
        showPauseDialog()
    }
}
