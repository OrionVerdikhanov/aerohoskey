package com.aerohockey.game.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aerohockey.game.ads.AdsManager
import com.aerohockey.game.databinding.ActivityMainBinding
import com.aerohockey.game.ui.about.AboutActivity
import com.aerohockey.game.ui.mode.ModeSelectionActivity
import com.aerohockey.game.ui.settings.SettingsActivity
import com.aerohockey.game.ui.stats.StatsActivity

/**
 * Главное меню приложения
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adsManager: AdsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация рекламы
        adsManager = AdsManager(this)
        adsManager.loadBanner(binding.bannerContainer)
        adsManager.loadInterstitial()

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnPlay.setOnClickListener {
            startActivity(Intent(this, ModeSelectionActivity::class.java))
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

    override fun onDestroy() {
        super.onDestroy()
        adsManager.destroy()
    }
}
