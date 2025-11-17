package com.aerohockey.game.domain.model

import kotlin.math.sqrt

/**
 * Двумерный вектор для позиций, скоростей и ускорений
 */
data class Vector2D(
    var x: Float = 0f,
    var y: Float = 0f
) {
    /** Длина вектора */
    fun length(): Float = sqrt(x * x + y * y)

    /** Квадрат длины (для оптимизации) */
    fun lengthSquared(): Float = x * x + y * y

    /** Нормализация вектора */
    fun normalize(): Vector2D {
        val len = length()
        return if (len > 0) Vector2D(x / len, y / len) else Vector2D(0f, 0f)
    }

    /** Умножение на скаляр */
    operator fun times(scalar: Float): Vector2D = Vector2D(x * scalar, y * scalar)

    /** Деление на скаляр */
    operator fun div(scalar: Float): Vector2D = Vector2D(x / scalar, y / scalar)

    /** Сложение векторов */
    operator fun plus(other: Vector2D): Vector2D = Vector2D(x + other.x, y + other.y)

    /** Вычитание векторов */
    operator fun minus(other: Vector2D): Vector2D = Vector2D(x - other.x, y - other.y)

    /** Скалярное произведение */
    fun dot(other: Vector2D): Float = x * other.x + y * other.y

    /** Расстояние до другой точки */
    fun distanceTo(other: Vector2D): Float = (this - other).length()

    /** Копирование значений */
    fun set(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    fun copy(): Vector2D = Vector2D(x, y)
}
