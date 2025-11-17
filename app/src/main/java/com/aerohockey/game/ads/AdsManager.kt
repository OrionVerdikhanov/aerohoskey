package com.aerohockey.game.ads

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener

/**
 * Менеджер для управления всеми типами рекламы Yandex
 */
class AdsManager(private val activity: Activity) {

    companion object {
        private const val TAG = "AdsManager"

        // ВНИМАНИЕ: Замените эти ID на реальные из кабинета РСЯ/Adfox
        private const val BANNER_AD_UNIT_ID = "demo-banner-yandex"
        private const val INTERSTITIAL_AD_UNIT_ID = "demo-interstitial-yandex"
        private const val REWARDED_AD_UNIT_ID = "demo-rewarded-yandex"
    }

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var bannerAdView: BannerAdView? = null

    /** Загрузка и показ баннера */
    fun loadBanner(container: ViewGroup) {
        bannerAdView = BannerAdView(activity).apply {
            setAdUnitId(BANNER_AD_UNIT_ID)
            setAdSize(AdSize.stickySize(activity))

            // Добавляем в контейнер
            container.removeAllViews()
            container.addView(this)

            // Загрузка рекламы
            loadAd(AdRequest.Builder().build())
        }

        Log.d(TAG, "Баннер загружается...")
    }

    /** Уничтожение баннера */
    fun destroyBanner() {
        bannerAdView?.destroy()
        bannerAdView = null
    }

    /** Загрузка межстраничной рекламы */
    fun loadInterstitial(onLoaded: (() -> Unit)? = null) {
        interstitialAd = InterstitialAd(activity).apply {
            setAdUnitId(INTERSTITIAL_AD_UNIT_ID)
            setInterstitialAdEventListener(object : InterstitialAdEventListener {
                override fun onAdLoaded() {
                    Log.d(TAG, "Interstitial загружена")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.e(TAG, "Ошибка загрузки Interstitial: ${error.description}")
                }

                override fun onAdShown() {
                    Log.d(TAG, "Interstitial показана")
                }

                override fun onAdDismissed() {
                    Log.d(TAG, "Interstitial закрыта")
                    // Перезагрузка для следующего показа
                    loadInterstitial()
                }

                override fun onAdClicked() {
                    Log.d(TAG, "Клик по Interstitial")
                }

                override fun onLeftApplication() {
                    Log.d(TAG, "Выход из приложения через Interstitial")
                }

                override fun onReturnedToApplication() {
                    Log.d(TAG, "Возврат в приложение из Interstitial")
                }

                override fun onImpression(data: ImpressionData?) {
                    Log.d(TAG, "Impressions Interstitial: ${data?.rawData}")
                }
            })

            loadAd(AdRequest.Builder().build())
        }
    }

    /** Показ межстраничной рекламы */
    fun showInterstitial(): Boolean {
        return if (interstitialAd?.isLoaded == true) {
            interstitialAd?.show()
            true
        } else {
            Log.w(TAG, "Interstitial еще не загружена")
            false
        }
    }

    /** Загрузка рекламы с вознаграждением */
    fun loadRewarded(onLoaded: (() -> Unit)? = null) {
        rewardedAd = RewardedAd(activity).apply {
            setAdUnitId(REWARDED_AD_UNIT_ID)
            setRewardedAdEventListener(object : RewardedAdEventListener {
                override fun onAdLoaded() {
                    Log.d(TAG, "Rewarded загружена")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.e(TAG, "Ошибка загрузки Rewarded: ${error.description}")
                }

                override fun onAdShown() {
                    Log.d(TAG, "Rewarded показана")
                }

                override fun onAdDismissed() {
                    Log.d(TAG, "Rewarded закрыта")
                    // Перезагрузка для следующего показа
                    loadRewarded()
                }

                override fun onRewarded(reward: Reward) {
                    Log.d(TAG, "Награда получена: ${reward.amount} ${reward.type}")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "Клик по Rewarded")
                }

                override fun onLeftApplication() {
                    Log.d(TAG, "Выход из приложения через Rewarded")
                }

                override fun onReturnedToApplication() {
                    Log.d(TAG, "Возврат в приложение из Rewarded")
                }

                override fun onImpression(data: ImpressionData?) {
                    Log.d(TAG, "Impressions Rewarded: ${data?.rawData}")
                }
            })

            loadAd(AdRequest.Builder().build())
        }
    }

    /** Показ рекламы с вознаграждением */
    fun showRewarded(onRewarded: () -> Unit): Boolean {
        return if (rewardedAd?.isLoaded == true) {
            // Переопределяем обработчик для текущей награды
            rewardedAd?.setRewardedAdEventListener(object : RewardedAdEventListener {
                override fun onRewarded(reward: Reward) {
                    Log.d(TAG, "Награда получена и передана пользователю")
                    onRewarded()
                }

                override fun onAdLoaded() {}
                override fun onAdFailedToLoad(error: AdRequestError) {}
                override fun onAdShown() {}
                override fun onAdDismissed() { loadRewarded() }
                override fun onAdClicked() {}
                override fun onLeftApplication() {}
                override fun onReturnedToApplication() {}
                override fun onImpression(data: ImpressionData?) {}
            })

            rewardedAd?.show()
            true
        } else {
            Log.w(TAG, "Rewarded еще не загружена")
            false
        }
    }

    /** Очистка ресурсов */
    fun destroy() {
        destroyBanner()
        interstitialAd = null
        rewardedAd = null
    }
}
