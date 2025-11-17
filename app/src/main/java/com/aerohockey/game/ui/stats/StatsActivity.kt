package com.aerohockey.game.ui.stats

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.StatsRepository
import com.aerohockey.game.databinding.ActivityStatsBinding
import kotlinx.coroutines.launch

/**
 * Экран статистики
 */
class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding
    private lateinit var statsRepository: StatsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statsRepository = StatsRepository(this)

        loadStats()
    }

    private fun loadStats() {
        lifecycleScope.launch {
            statsRepository.statsFlow.collect { stats ->
                binding.tvTotalGames.text = getString(
                    R.string.stats_total_games,
                    stats.totalGames
                )

                binding.tvPlayer1Wins.text = getString(
                    R.string.stats_player1_wins,
                    stats.player1Wins,
                    stats.player1WinRate
                )

                binding.tvPlayer2Wins.text = getString(
                    R.string.stats_player2_wins,
                    stats.player2Wins,
                    stats.player2WinRate
                )

                // Обновление прогресс-баров
                binding.progressPlayer1.progress = stats.player1WinRate
                binding.progressPlayer2.progress = stats.player2WinRate
            }
        }
    }
}
