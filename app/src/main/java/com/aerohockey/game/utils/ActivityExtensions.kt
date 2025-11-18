package com.aerohockey.game.utils

import android.app.Activity
import android.content.Intent
import com.aerohockey.game.R

/**
 * Расширения для Activity для красивых переходов
 */

/**
 * Запустить activity с анимацией slide in
 */
fun Activity.startActivityWithSlideIn(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
}

/**
 * Завершить activity с анимацией slide out
 */
fun Activity.finishWithSlideOut() {
    finish()
    overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left)
}

/**
 * Запустить activity с анимацией fade
 */
fun Activity.startActivityWithFade(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}

/**
 * Завершить activity с анимацией fade
 */
fun Activity.finishWithFade() {
    finish()
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}

/**
 * Запустить activity с анимацией scale
 */
fun Activity.startActivityWithScale(intent: Intent) {
    startActivity(intent)
    overridePendingTransition(R.anim.scale_in, R.anim.fade_out)
}
