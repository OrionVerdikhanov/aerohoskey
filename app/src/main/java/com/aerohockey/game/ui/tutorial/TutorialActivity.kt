package com.aerohockey.game.ui.tutorial

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.aerohockey.game.R
import com.aerohockey.game.core.config.GameConfig
import com.aerohockey.game.databinding.ActivityTutorialBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Экран обучения/туториала с ViewPager2
 */
class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var adapter: TutorialPagerAdapter
    private val slides = listOf(
        TutorialSlide(
            icon = "🏒",
            titleResId = R.string.tutorial_slide1_title,
            descriptionResId = R.string.tutorial_slide1_desc
        ),
        TutorialSlide(
            icon = "👆",
            titleResId = R.string.tutorial_slide2_title,
            descriptionResId = R.string.tutorial_slide2_desc
        ),
        TutorialSlide(
            icon = "⚡",
            titleResId = R.string.tutorial_slide3_title,
            descriptionResId = R.string.tutorial_slide3_desc
        ),
        TutorialSlide(
            icon = "🏆",
            titleResId = R.string.tutorial_slide4_title,
            descriptionResId = R.string.tutorial_slide4_desc
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupIndicator()
        setupButtons()
    }

    /**
     * Настройка ViewPager
     */
    private fun setupViewPager() {
        adapter = TutorialPagerAdapter(slides)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
                updateButtons(position)
            }
        })
    }

    /**
     * Настройка индикатора страниц (точки)
     */
    private fun setupIndicator() {
        val indicators = arrayOfNulls<ImageView>(slides.size)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.gradient_accent)
            )
            indicators[i]?.layoutParams = layoutParams
            binding.dotsIndicator.addView(indicators[i])
        }

        updateIndicator(0)
    }

    /**
     * Обновление индикатора
     */
    private fun updateIndicator(position: Int) {
        for (i in 0 until binding.dotsIndicator.childCount) {
            val dot = binding.dotsIndicator.getChildAt(i) as ImageView
            if (i == position) {
                dot.alpha = 1.0f
                dot.scaleX = 1.2f
                dot.scaleY = 1.2f
            } else {
                dot.alpha = 0.4f
                dot.scaleX = 1.0f
                dot.scaleY = 1.0f
            }
        }
    }

    /**
     * Настройка кнопок
     */
    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            finishTutorial()
        }

        binding.btnNext.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition < slides.size - 1) {
                binding.viewPager.currentItem = currentPosition + 1
            } else {
                finishTutorial()
            }
        }
    }

    /**
     * Обновление кнопок
     */
    private fun updateButtons(position: Int) {
        if (position == slides.size - 1) {
            binding.btnNext.text = getString(R.string.tutorial_start)
            binding.btnSkip.visibility = View.GONE
        } else {
            binding.btnNext.text = getString(R.string.tutorial_next)
            binding.btnSkip.visibility = View.VISIBLE
        }
    }

    /**
     * Завершение туториала
     */
    private fun finishTutorial() {
        // Сохранить, что туториал был показан
        lifecycleScope.launch {
            GameConfig.dataStore(this@TutorialActivity).edit { preferences ->
                preferences[booleanPreferencesKey("tutorial_completed")] = true
            }
        }
        finish()
    }

    companion object {
        /**
         * Проверить, нужно ли показывать туториал
         */
        suspend fun shouldShowTutorial(context: Context): Boolean {
            val preferences = GameConfig.dataStore(context).data.first()
            return !preferences[booleanPreferencesKey("tutorial_completed")] ?: false
        }
    }
}
