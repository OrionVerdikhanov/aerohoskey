package com.aerohockey.game.ui.game

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import com.aerohockey.game.core.config.GameConfig

/**
 * Игровой поток
 * Отвечает за игровой цикл с фиксированным FPS
 */
class GameLoopThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameController: GameController,
    private val renderer: GameRenderer
) : Thread() {

    @Volatile
    private var running = false

    private val targetFrameTime = GameConfig.TARGET_FRAME_TIME
    private var fps = 0
    private var frameCount = 0
    private var fpsTimer = 0L

    init {
        name = "GameLoopThread"
    }

    /**
     * Запустить поток
     */
    fun startLoop() {
        running = true
        start()
    }

    /**
     * Остановить поток
     */
    fun stopLoop() {
        running = false
        try {
            join(1000)
        } catch (e: InterruptedException) {
            Log.e(TAG, "Ошибка остановки потока", e)
        }
    }

    override fun run() {
        var lastTime = System.currentTimeMillis()
        fpsTimer = lastTime

        while (running) {
            val startTime = System.currentTimeMillis()
            val deltaTime = (startTime - lastTime) / 1000f
            lastTime = startTime

            try {
                // Обновление логики
                update(deltaTime)

                // Отрисовка
                render()

                // Подсчет FPS
                calculateFPS(startTime)

                // Контроль частоты кадров
                controlFrameRate(startTime)

            } catch (e: Exception) {
                Log.e(TAG, "Ошибка в игровом цикле", e)
            }
        }
    }

    /**
     * Обновление логики игры
     */
    private fun update(deltaTime: Float) {
        gameController.update(deltaTime)
    }

    /**
     * Отрисовка кадра
     */
    private fun render() {
        var canvas: Canvas? = null
        try {
            canvas = surfaceHolder.lockCanvas()
            canvas?.let {
                synchronized(surfaceHolder) {
                    renderer.render(it, gameController)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка отрисовки", e)
        } finally {
            canvas?.let {
                try {
                    surfaceHolder.unlockCanvasAndPost(it)
                } catch (e: Exception) {
                    Log.e(TAG, "Ошибка unlockCanvas", e)
                }
            }
        }
    }

    /**
     * Подсчет FPS
     */
    private fun calculateFPS(currentTime: Long) {
        frameCount++
        if (currentTime - fpsTimer > 1000) {
            fps = frameCount
            frameCount = 0
            fpsTimer = currentTime
            // Log.d(TAG, "FPS: $fps")
        }
    }

    /**
     * Контроль частоты кадров
     */
    private fun controlFrameRate(startTime: Long) {
        val elapsed = System.currentTimeMillis() - startTime
        val sleepTime = targetFrameTime - elapsed

        if (sleepTime > 0) {
            try {
                sleep(sleepTime)
            } catch (e: InterruptedException) {
                Log.e(TAG, "Прерывание сна потока", e)
            }
        }
    }

    /**
     * Получить текущий FPS
     */
    fun getCurrentFPS(): Int = fps

    companion object {
        private const val TAG = "GameLoopThread"
    }
}
