package com.aerohockey.game.domain.model

import android.graphics.RectF

/**
 * Модель ворот
 */
data class Goal(
    val playerId: Int, // 1 или 2 (чьи это ворота)
    val bounds: RectF = RectF()
) {
    /** Проверка, попала ли шайба в ворота */
    fun isPuckInside(puck: Puck): Boolean {
        val puckCenterX = puck.position.x
        val puckCenterY = puck.position.y

        return puckCenterX >= bounds.left &&
                puckCenterX <= bounds.right &&
                puckCenterY >= bounds.top &&
                puckCenterY <= bounds.bottom
    }

    /** Установка границ ворот */
    fun setBounds(left: Float, top: Float, right: Float, bottom: Float) {
        bounds.set(left, top, right, bottom)
    }
}
