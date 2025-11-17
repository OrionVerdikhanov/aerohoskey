package com.aerohockey.game.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aerohockey.game.R
import com.aerohockey.game.ads.AdsManager
import com.aerohockey.game.databinding.ActivityResultBinding
import com.aerohockey.game.ui.game.GameActivity
import com.aerohockey.game.ui.menu.MainActivity

/**
 * Экран результатов игры
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var adsManager: AdsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация рекламы
        adsManager = AdsManager(this)
        adsManager.loadInterstitial()

        val winnerId = intent.getIntExtra(EXTRA_WINNER_ID, 1)
        val player1Score = intent.getIntExtra(EXTRA_PLAYER1_SCORE, 0)
        val player2Score = intent.getIntExtra(EXTRA_PLAYER2_SCORE, 0)

        displayResults(winnerId, player1Score, player2Score)
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
    }
}
