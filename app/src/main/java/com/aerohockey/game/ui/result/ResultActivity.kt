package com.aerohockey.game.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.ads.AdsManager
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.databinding.ActivityResultBinding
import com.aerohockey.game.domain.progression.PlayerLevel
import com.aerohockey.game.ui.game.GameActivity
import com.aerohockey.game.ui.menu.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Экран результатов игры
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var adsManager: AdsManager
    private lateinit var progressRepository: PlayerProgressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация рекламы
        adsManager = AdsManager(this)
        adsManager.loadInterstitial()

        // Инициализация репозитория прогресса
        progressRepository = PlayerProgressRepository(this)

        val winnerId = intent.getIntExtra(EXTRA_WINNER_ID, 1)
        val player1Score = intent.getIntExtra(EXTRA_PLAYER1_SCORE, 0)
        val player2Score = intent.getIntExtra(EXTRA_PLAYER2_SCORE, 0)
        val maxCombo = intent.getIntExtra(EXTRA_MAX_COMBO, 0)
        val isPerfectGame = intent.getBooleanExtra(EXTRA_PERFECT_GAME, false)

        displayResults(winnerId, player1Score, player2Score)
        awardXP(winnerId, player1Score, player2Score, maxCombo, isPerfectGame)
        setupButtons()
    }

    private fun displayResults(winnerId: Int, player1Score: Int, player2Score: Int) {
        binding.tvWinner.text = getString(R.string.result_winner, winnerId)
        binding.tvScore.text = getString(R.string.result_score, player1Score, player2Score)

        // Цвет победителя
        val winnerColor = if (winnerId == 1) {
            getColor(R.color.player1_color)
        } else {
            getColor(R.color.player2_color)
        }
        binding.tvWinner.setTextColor(winnerColor)
    }

    private fun awardXP(winnerId: Int, player1Score: Int, player2Score: Int, maxCombo: Int, isPerfectGame: Boolean) {
        lifecycleScope.launch {
            var totalXP = 0

            // XP за победу (только если играет Player 1)
            if (winnerId == 1) {
                totalXP += PlayerLevel.XP_WIN
            }

            // XP за каждый забитый гол (только Player 1)
            totalXP += player1Score * PlayerLevel.XP_GOAL

            // Бонус за идеальную игру
            if (isPerfectGame && winnerId == 1) {
                totalXP += PlayerLevel.XP_PERFECT_GAME
            }

            // Применяем множитель комбо если был
            val comboMultiplier = when (maxCombo) {
                in 2..3 -> 1.5f
                in 4..5 -> 2.0f
                in 6..7 -> 2.5f
                in 8..Int.MAX_VALUE -> 3.0f
                else -> 1.0f
            }

            // Добавляем XP с учетом множителя
            if (totalXP > 0) {
                val result = progressRepository.addXP(totalXP, comboMultiplier)

                // Показываем уведомление о полученном XP
                val message = if (result.leveledUp) {
                    "🎉 LEVEL UP! Уровень ${result.newLevel}\n⭐ +${result.xpGained} XP"
                } else {
                    "⭐ +${result.xpGained} XP"
                }

                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupButtons() {
        binding.btnPlayAgain.setOnClickListener {
            // Показываем interstitial перед новой игрой
            if (!adsManager.showInterstitial()) {
                startNewGame()
            } else {
                // Реклама показана, после закрытия пользователь сам нажмет кнопку снова
                startNewGame()
            }
        }

        binding.btnMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun startNewGame() {
        startActivity(Intent(this, GameActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        adsManager.destroy()
    }

    companion object {
        const val EXTRA_WINNER_ID = "winner_id"
        const val EXTRA_PLAYER1_SCORE = "player1_score"
        const val EXTRA_PLAYER2_SCORE = "player2_score"
        const val EXTRA_MAX_COMBO = "max_combo"
        const val EXTRA_PERFECT_GAME = "perfect_game"
    }
}
