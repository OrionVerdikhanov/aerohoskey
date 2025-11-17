# Руководство по интеграции Yandex Mobile Ads SDK

## Текущая конфигурация

### Версия SDK
```kotlin
implementation("com.yandex.android:mobileads:7.6.1")
```

### Инициализация
Инициализация SDK происходит автоматически в классе `AeroHockeyApplication`:

```kotlin
// app/src/main/java/com/aerohockey/game/AeroHockeyApplication.kt
MobileAds.initialize(this) {
    Log.d(TAG, "Yandex Mobile Ads SDK успешно инициализирован")
}
```

## Настройка рекламных блоков

### Шаг 1: Регистрация в Yandex РСЯ

1. Перейдите на https://partner.yandex.ru/
2. Зарегистрируйтесь или войдите
3. Создайте новое приложение
4. Получите ID приложения

### Шаг 2: Создание рекламных блоков

Создайте три рекламных блока:

1. **Баннер** (Адаптивный sticky-баннер)
   - Формат: Баннер
   - Размер: Адаптивный
   - Расположение: Внизу экрана

2. **Interstitial** (Полноэкранная реклама)
   - Формат: Межстраничная реклама
   - Использование: При повторной игре

3. **Rewarded** (Реклама с вознаграждением)
   - Формат: Реклама с вознаграждением
   - Использование: Продолжить игру (будущая реализация)

### Шаг 3: Замена тестовых ID

Откройте файл `app/src/main/java/com/aerohockey/game/ads/AdsManager.kt` и замените:

```kotlin
companion object {
    // ❌ ТЕСТОВЫЕ ID - ЗАМЕНИТЬ НА РЕАЛЬНЫЕ
    private const val BANNER_AD_UNIT_ID = "demo-banner-yandex"
    private const val INTERSTITIAL_AD_UNIT_ID = "demo-interstitial-yandex"
    private const val REWARDED_AD_UNIT_ID = "demo-rewarded-yandex"
}
```

На:

```kotlin
companion object {
    // ✅ РЕАЛЬНЫЕ ID из кабинета РСЯ
    private const val BANNER_AD_UNIT_ID = "R-M-XXXXXX-X"
    private const val INTERSTITIAL_AD_UNIT_ID = "R-M-XXXXXX-X"
    private const val REWARDED_AD_UNIT_ID = "R-M-XXXXXX-X"
}
```

## Проверка интеграции

### Метод 1: Logcat

Запустите приложение и проверьте logcat:

```bash
adb logcat | grep "Yandex\|AdsManager"
```

Ожидаемый вывод:
```
D/AeroHockeyApp: Инициализация Yandex Ads SDK запущена
D/AeroHockeyApp: Yandex Mobile Ads SDK успешно инициализирован
D/AdsManager: Баннер загружается...
D/AdsManager: Interstitial загружена
```

### Метод 2: Визуальная проверка

1. **Главное меню**: Внизу должен отображаться баннер
2. **Результаты игры**: При нажатии "Играть ещё" может показаться interstitial
3. Убедитесь, что реклама не перекрывает основной контент

### Метод 3: Проверка ошибок

Если реклама не загружается:

```bash
adb logcat | grep "AdRequestError"
```

## Настройка для детских приложений

Если приложение предназначено для детей младше 13 лет, измените в `AndroidManifest.xml`:

```xml
<meta-data
    android:name="@string/yandex_mobileads_age_restricted_user"
    android:value="true" />
```

## Отключение рекламного ID (опционально)

Для детских приложений добавьте в `AndroidManifest.xml`:

```xml
<uses-permission android:name="com.google.android.gms.permission.AD_ID"
    tools:node="remove" />
```

## Тестирование на разных устройствах

### Минимальные требования
- Android 5.0 (API 21) и выше
- Интернет-соединение

### Рекомендуемые разрешения экрана
- 1280x720 (HD)
- 1920x1080 (Full HD)
- 2560x1440 (2K)

## Монетизационная стратегия

### Текущая реализация

1. **Баннер на главном экране**
   - Всегда показывается
   - Не мешает навигации
   - Генерирует постоянный доход

2. **Interstitial при повторной игре**
   - Показывается не чаще 1 раза в 3 минуты
   - Не раздражает пользователя
   - Приносит основной доход

### Рекомендации по оптимизации

1. **Не показывайте рекламу слишком часто**
   - Это ухудшает пользовательский опыт
   - Может привести к удалению приложения

2. **Используйте медиацию**
   - Подключите несколько рекламных сетей
   - Увеличьте fill rate

3. **A/B тестирование**
   - Тестируйте разные стратегии показа
   - Анализируйте метрики

## Метрики для отслеживания

1. **Impressions** — количество показов
2. **CTR** — процент кликов
3. **eCPM** — эффективная стоимость 1000 показов
4. **Fill Rate** — процент заполнения рекламных запросов

## Полезные ссылки

- [Документация Yandex Mobile Ads SDK](https://yandex.ru/dev/mobile-ads/)
- [Кабинет партнера РСЯ](https://partner.yandex.ru/)
- [Политики Google Play](https://support.google.com/googleplay/android-developer/answer/9857753)

## Контрольный список перед публикацией

- [ ] Заменены все тестовые ID на реальные
- [ ] Протестировано на реальных устройствах
- [ ] Проверена интеграция через logcat
- [ ] Настроена политика конфиденциальности
- [ ] Указана категория приложения (не для детей / для детей)
- [ ] Добавлена ссылка на политику конфиденциальности в Google Play
- [ ] Проверено, что реклама не мешает игровому процессу
- [ ] Настроены ProGuard правила для релизной сборки

## Поддержка

По вопросам интеграции:
- **Email:** support@aerohockey.com
- **Документация Yandex:** https://yandex.ru/dev/mobile-ads/doc/android/quick-start.html
