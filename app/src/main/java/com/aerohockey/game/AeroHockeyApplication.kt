package com.aerohockey.game

import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds

/**
 * Класс Application для инициализации Yandex Mobile Ads SDK
 * и других глобальных компонентов приложения
 */
class AeroHockeyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализация Yandex Mobile Ads SDK
        initializeYandexAds()
    }

    private fun initializeYandexAds() {
        try {
            // Включаем индикатор устаревшей версии SDK (для разработки)
            // В релизе можно отключить: MobileAds.enableDebugErrorIndicator(false)
            MobileAds.enableDebugErrorIndicator(true)

            // Инициализация SDK
            MobileAds.initialize(this) {
                Log.d(TAG, "Yandex Mobile Ads SDK успешно инициализирован")
            }

            Log.d(TAG, "Инициализация Yandex Ads SDK запущена")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка инициализации Yandex Ads SDK", e)
        }
    }

    companion object {
        private const val TAG = "AeroHockeyApp"
    }
}
