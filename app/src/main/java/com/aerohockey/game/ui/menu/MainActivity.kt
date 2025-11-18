package com.aerohockey.game.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.ads.AdsManager
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.databinding.ActivityMainBinding
import com.aerohockey.game.domain.progression.PlayerLevel
import com.aerohockey.game.ui.about.AboutActivity
import com.aerohockey.game.ui.leaderboard.LeaderboardActivity
import com.aerohockey.game.ui.mode.ModeSelectionActivity
import com.aerohockey.game.ui.quests.QuestsActivity
import com.aerohockey.game.ui.settings.SettingsActivity
import com.aerohockey.game.ui.shop.ShopActivity
import com.aerohockey.game.ui.stats.StatsActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Главное меню приложения
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adsManager: AdsManager
    private lateinit var progressRepository: PlayerProgressRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация рекламы
        adsManager = AdsManager(this)
        adsManager.loadBanner(binding.bannerContainer)
        adsManager.loadInterstitial()

        // Инициализация репозитория прогресса
        progressRepository = PlayerProgressRepository(this)

        setupButtons()
        loadPlayerProgress()
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, ModeSelectionActivity::class.java))
        }

        binding.btnShop.setOnClickListener {
            startActivity(Intent(this, ShopActivity::class.java))
        }

        binding.btnQuests.setOnClickListener {
            startActivity(Intent(this, QuestsActivity::class.java))
        }

        binding.btnLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnStats.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }

        binding.btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    private fun loadPlayerProgress() {
        lifecycleScope.launch {
            // Загрузка уровня и XP
            progressRepository.getPlayerLevel().collect { playerLevel ->
                updatePlayerLevelWidget(playerLevel)
            }
        }

        lifecycleScope.launch {
            // Загрузка монет
            progressRepository.getCoins().collect { coins ->
                binding.playerLevelWidget.tvCoins.text = coins.toString()
            }
        }
    }

    private fun updatePlayerLevelWidget(playerLevel: PlayerLevel) {
        with(binding.playerLevelWidget) {
            // Иконка ранга с анимацией смены
            val newRankIcon = when (playerLevel.getRank()) {
                PlayerLevel.Rank.ROOKIE -> "🏅"
                PlayerLevel.Rank.AMATEUR -> "🥉"
                PlayerLevel.Rank.PROFESSIONAL -> "🥈"
                PlayerLevel.Rank.EXPERT -> "🥇"
                PlayerLevel.Rank.MASTER -> "👑"
            }

            if (tvRankIcon.text != newRankIcon) {
                // Анимация смены ранга
                tvRankIcon.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(150)
                    .withEndAction {
                        tvRankIcon.text = newRankIcon
                        tvRankIcon.animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(200)
                            .withEndAction {
                                tvRankIcon.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start()
                            }
                            .start()
                    }
                    .start()
            } else {
                tvRankIcon.text = newRankIcon
            }

            // Уровень с плавной анимацией
            tvLevel.text = playerLevel.currentLevel.toString()

            // Прогресс бар XP с анимацией
            val xpForNextLevel = playerLevel.getXPForNextLevel()
            progressXP.max = xpForNextLevel

            // Плавная анимация прогресс бара
            android.animation.ObjectAnimator.ofInt(progressXP, "progress", progressXP.progress, playerLevel.currentXP).apply {
                duration = 500
                start()
            }

            // Текст прогресса
            tvXPProgress.text = "${playerLevel.currentXP}/$xpForNextLevel XP"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adsManager.destroy()
    }
}
