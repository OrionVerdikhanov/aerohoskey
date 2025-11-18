package com.aerohockey.game.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aerohockey.game.databinding.ActivitySplashBinding
import com.aerohockey.game.ui.menu.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash screen с красивой анимацией
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Скрыть action bar
        supportActionBar?.hide()

        // Запустить анимацию
        startAnimations()

        // Переход к главному экрану через 3 секунды
        lifecycleScope.launch {
            delay(3000)
            navigateToMain()
        }
    }

    /**
     * Запуск анимаций
     */
    private fun startAnimations() {
        // Анимация логотипа
        animateLogo()

        // Анимация кругов
        animateCircles()

        // Анимация подзаголовка
        animateSubtext()
    }

    /**
     * Анимация логотипа
     */
    private fun animateLogo() {
        // Иконка - масштаб + вращение
        val scaleX = ObjectAnimator.ofFloat(binding.logoIcon, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.logoIcon, View.SCALE_Y, 0f, 1f)
        val rotation = ObjectAnimator.ofFloat(binding.logoIcon, View.ROTATION, -180f, 0f)

        val iconSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation)
            duration = 800
            interpolator = OvershootInterpolator()
        }

        // Текст - появление снизу
        binding.logoText.translationY = 100f
        binding.logoText.alpha = 0f

        val textTranslation = ObjectAnimator.ofFloat(binding.logoText, View.TRANSLATION_Y, 100f, 0f)
        val textAlpha = ObjectAnimator.ofFloat(binding.logoText, View.ALPHA, 0f, 1f)

        val textSet = AnimatorSet().apply {
            playTogether(textTranslation, textAlpha)
            duration = 600
            startDelay = 400
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Запуск
        AnimatorSet().apply {
            playSequentially(iconSet, textSet)
            start()
        }
    }

    /**
     * Анимация декоративных кругов
     */
    private fun animateCircles() {
        // Круг 1 - пульсация
        val pulse1 = ObjectAnimator.ofFloat(binding.circle1, View.SCALE_X, 1f, 1.2f, 1f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val pulse1Y = ObjectAnimator.ofFloat(binding.circle1, View.SCALE_Y, 1f, 1.2f, 1f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Круг 2 - пульсация с задержкой
        val pulse2 = ObjectAnimator.ofFloat(binding.circle2, View.SCALE_X, 1f, 1.3f, 1f).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            startDelay = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        val pulse2Y = ObjectAnimator.ofFloat(binding.circle2, View.SCALE_Y, 1f, 1.3f, 1f).apply {
            duration = 2500
            repeatCount = ObjectAnimator.INFINITE
            startDelay = 500
            interpolator = AccelerateDecelerateInterpolator()
        }

        pulse1.start()
        pulse1Y.start()
        pulse2.start()
        pulse2Y.start()
    }

    /**
     * Анимация подзаголовка
     */
    private fun animateSubtext() {
        ObjectAnimator.ofFloat(binding.logoSubtext, View.ALPHA, 0f, 1f).apply {
            duration = 600
            startDelay = 1200
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Переход к главному экрану
     */
    private fun navigateToMain() {
        // Анимация исчезновения
        val fadeOut = ObjectAnimator.ofFloat(binding.root, View.ALPHA, 1f, 0f).apply {
            duration = 300
        }

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        })

        fadeOut.start()
    }
}
