package com.aerohockey.game.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.StatsRepository
import com.aerohockey.game.databinding.ActivitySettingsBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Экран настроек
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var statsRepository: StatsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statsRepository = StatsRepository(this)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val stats = statsRepository.statsFlow.first()
            binding.switchSound.isChecked = stats.soundEnabled
            binding.switchVibration.isChecked = stats.vibrationEnabled
        }
    }

    private fun setupListeners() {
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                statsRepository.setSoundEnabled(isChecked)
            }
        }

        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                statsRepository.setVibrationEnabled(isChecked)
            }
        }

        binding.btnResetStats.setOnClickListener {
            showResetConfirmationDialog()
        }
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.settings_reset_stats)
            .setMessage(R.string.settings_reset_confirm)
            .setPositiveButton(R.string.settings_yes) { _, _ ->
                lifecycleScope.launch {
                    statsRepository.resetStats()
                }
            }
            .setNegativeButton(R.string.settings_no, null)
            .show()
    }
}
