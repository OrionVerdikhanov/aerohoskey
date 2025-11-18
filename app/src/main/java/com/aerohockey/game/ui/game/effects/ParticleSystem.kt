package com.aerohockey.game.ui.game.effects

import android.graphics.Canvas
import android.graphics.Paint
import com.aerohockey.game.domain.model.Vector2D
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Система частиц для визуальных эффектов
 * Используется для взрывов при голах, столкновениях, power-ups
 */
class ParticleSystem(private val maxParticles: Int = 100) {

    private val particles = mutableListOf<Particle>()
    private val particlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Создать взрыв частиц при голе
     */
    fun createGoalExplosion(x: Float, y: Float, color: Int) {
        val particleCount = 50
        for (i in 0 until particleCount) {
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 400f + 200f
            val vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed
            val vy = sin(Math.toRadians(angle.toDouble())).toFloat() * speed

            particles.add(
                Particle(
                    position = Vector2D(x, y),
                    velocity = Vector2D(vx, vy),
                    color = color,
                    lifetime = Random.nextFloat() * 1000f + 500f,
                    size = Random.nextFloat() * 8f + 4f,
                    type = ParticleType.EXPLOSION
                )
            )
        }
        limitParticles()
    }

    /**
     * Создать эффект столкновения
     */
    fun createCollisionEffect(x: Float, y: Float, normalX: Float, normalY: Float, color: Int) {
        val particleCount = 15
        for (i in 0 until particleCount) {
            val spread = 60f // градусы разброса
            val baseAngle = Math.toDegrees(kotlin.math.atan2(normalY.toDouble(), normalX.toDouble())).toFloat()
            val angle = baseAngle + Random.nextFloat() * spread - spread / 2
            val speed = Random.nextFloat() * 300f + 100f
            val vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed
            val vy = sin(Math.toRadians(angle.toDouble())).toFloat() * speed

            particles.add(
                Particle(
                    position = Vector2D(x, y),
                    velocity = Vector2D(vx, vy),
                    color = color,
                    lifetime = Random.nextFloat() * 300f + 200f,
                    size = Random.nextFloat() * 4f + 2f,
                    type = ParticleType.SPARK
                )
            )
        }
        limitParticles()
    }

    /**
     * Создать эффект для power-up
     */
    fun createPowerUpEffect(x: Float, y: Float, color: Int) {
        val particleCount = 20
        for (i in 0 until particleCount) {
            val angle = Random.nextFloat() * 360f
            val speed = Random.nextFloat() * 150f + 50f
            val vx = cos(Math.toRadians(angle.toDouble())).toFloat() * speed
            val vy = sin(Math.toRadians(angle.toDouble())).toFloat() * speed

            particles.add(
                Particle(
                    position = Vector2D(x, y),
                    velocity = Vector2D(vx, vy),
                    color = color,
                    lifetime = Random.nextFloat() * 800f + 400f,
                    size = Random.nextFloat() * 6f + 3f,
                    type = ParticleType.GLOW
                )
            )
        }
        limitParticles()
    }

    /**
     * Создать постоянный эффект свечения вокруг объекта
     */
    fun createGlowEffect(x: Float, y: Float, radius: Float, color: Int) {
        if (particles.size >= maxParticles) return

        val angle = Random.nextFloat() * 360f
        val distance = Random.nextFloat() * radius
        val px = x + cos(Math.toRadians(angle.toDouble())).toFloat() * distance
        val py = y + sin(Math.toRadians(angle.toDouble())).toFloat() * distance

        particles.add(
            Particle(
                position = Vector2D(px, py),
                velocity = Vector2D(0f, -20f), // Медленно вверх
                color = color,
                lifetime = 500f,
                size = Random.nextFloat() * 3f + 1f,
                type = ParticleType.GLOW
            )
        )
    }

    /**
     * Обновить все частицы
     */
    fun update(deltaTime: Float) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()

            // Обновление позиции
            particle.position.x += particle.velocity.x * deltaTime
            particle.position.y += particle.velocity.y * deltaTime

            // Применение гравитации для взрывов
            if (particle.type == ParticleType.EXPLOSION) {
                particle.velocity.y += 500f * deltaTime // Гравитация
            }

            // Затухание скорости
            particle.velocity.x *= 0.98f
            particle.velocity.y *= 0.98f

            // Уменьшение времени жизни
            particle.age += deltaTime * 1000f

            // Удаление мертвых частиц
            if (particle.age >= particle.lifetime) {
                iterator.remove()
            }
        }
    }

    /**
     * Отрисовать все частицы
     */
    fun render(canvas: Canvas) {
        particles.forEach { particle ->
            val alpha = ((1f - particle.age / particle.lifetime) * 255).toInt().coerceIn(0, 255)
            particlePaint.color = particle.color
            particlePaint.alpha = alpha

            when (particle.type) {
                ParticleType.EXPLOSION, ParticleType.SPARK -> {
                    canvas.drawCircle(
                        particle.position.x,
                        particle.position.y,
                        particle.size,
                        particlePaint
                    )
                }
                ParticleType.GLOW -> {
                    // Рисуем с эффектом свечения (несколько слоев)
                    particlePaint.alpha = alpha / 3
                    canvas.drawCircle(
                        particle.position.x,
                        particle.position.y,
                        particle.size * 2f,
                        particlePaint
                    )
                    particlePaint.alpha = alpha
                    canvas.drawCircle(
                        particle.position.x,
                        particle.position.y,
                        particle.size,
                        particlePaint
                    )
                }
            }
        }
    }

    /**
     * Ограничить количество частиц
     */
    private fun limitParticles() {
        while (particles.size > maxParticles) {
            particles.removeAt(0)
        }
    }

    /**
     * Очистить все частицы
     */
    fun clear() {
        particles.clear()
    }

    /**
     * Получить количество активных частиц
     */
    fun getActiveCount(): Int = particles.size
}

/**
 * Одна частица
 */
private data class Particle(
    val position: Vector2D,
    val velocity: Vector2D,
    val color: Int,
    val lifetime: Float,
    val size: Float,
    val type: ParticleType,
    var age: Float = 0f
)

/**
 * Типы частиц
 */
private enum class ParticleType {
    EXPLOSION,  // Взрыв при голе
    SPARK,      // Искры при столкновении
    GLOW        // Свечение
}
