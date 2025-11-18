package com.aerohockey.game.ui.leaderboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.databinding.ActivityLeaderboardBinding
import com.aerohockey.game.domain.progression.LeaderboardCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Экран таблицы лидеров и рекордов
 */
class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var progressRepository: PlayerProgressRepository
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    private var currentCategory = LeaderboardCategory.HIGHEST_COMBO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressRepository = PlayerProgressRepository(this)

        setupToolbar()
        setupRecyclerView()
        setupTabs()
        loadLeaderboard()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter()

        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(this@LeaderboardActivity)
            adapter = leaderboardAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                currentCategory = when (tab?.position) {
                    0 -> LeaderboardCategory.HIGHEST_COMBO
                    1 -> LeaderboardCategory.FASTEST_WIN
                    2 -> LeaderboardCategory.MOST_GOALS_MATCH
                    3 -> LeaderboardCategory.LONGEST_MATCH
                    4 -> LeaderboardCategory.MOST_POWERUPS
                    else -> LeaderboardCategory.HIGHEST_COMBO
                }
                loadLeaderboard()
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun loadLeaderboard() {
        lifecycleScope.launch {
            val leaderboard = progressRepository.getLeaderboard(currentCategory).first()

            // Обновляем топ 3
            if (leaderboard.entries.isNotEmpty()) {
                val top3 = leaderboard.entries.take(3)

                binding.tvFirstPlace.text = if (top3.isNotEmpty()) {
                    "${top3[0].playerName}\n${top3[0].score}"
                } else "—"

                binding.tvSecondPlace.text = if (top3.size > 1) {
                    "${top3[1].playerName}\n${top3[1].score}"
                } else "—"

                binding.tvThirdPlace.text = if (top3.size > 2) {
                    "${top3[2].playerName}\n${top3[2].score}"
                } else "—"

                // Обновляем список остальных
                val rest = if (leaderboard.entries.size > 3) {
                    leaderboard.entries.drop(3)
                } else {
                    emptyList()
                }
                leaderboardAdapter.submitList(rest)
            } else {
                binding.tvFirstPlace.text = "—"
                binding.tvSecondPlace.text = "—"
                binding.tvThirdPlace.text = "—"
                leaderboardAdapter.submitList(emptyList())
            }
        }
    }
}
