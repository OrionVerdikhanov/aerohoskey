package com.aerohockey.game.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aerohockey.game.BuildConfig
import com.aerohockey.game.R
import com.aerohockey.game.databinding.ActivityAboutBinding

/**
 * Экран "О приложении"
 */
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInfo()
    }

    private fun setupInfo() {
        // Отображение версии приложения
        binding.tvVersion.text = getString(
            R.string.about_version,
            BuildConfig.VERSION_NAME
        )

        // Ссылка на политику конфиденциальности (пример)
        binding.tvPrivacy.setOnClickListener {
            // Замените URL на реальную ссылку на вашу политику конфиденциальности
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://example.com/privacy")
            }
            startActivity(intent)
        }
    }
}
