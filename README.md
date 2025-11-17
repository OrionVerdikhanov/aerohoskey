# Аэрохоккей — Игра для Android

Классическая игра "Аэрохоккей" для двух игроков на одном устройстве Android с интеграцией Yandex Mobile Ads SDK.

## Особенности

- ✅ **Игра для двух игроков** на одном экране (мультитач)
- ✅ **Реалистичная физика** шайбы и столкновений
- ✅ **Современный UI/UX** с Material Design
- ✅ **Интеграция Yandex Mobile Ads SDK** (баннеры, interstitial, rewarded)
- ✅ **Статистика игр** с сохранением через DataStore
- ✅ **Настройки** звука и вибрации
- ✅ **Чистая архитектура** MVVM с разделением на слои

## Технологический стек

- **Язык:** Kotlin 1.9.22
- **Android Gradle Plugin:** 8.7.3
- **minSdk:** 21
- **compileSdk/targetSdk:** 34
- **ViewBinding:** включен
- **Архитектура:** MVVM
- **Зависимости:**
  - AndroidX (Core, AppCompat, Material, ConstraintLayout)
  - Lifecycle & ViewModel
  - Kotlin Coroutines
  - DataStore Preferences
  - Yandex Mobile Ads SDK 7.6.1

## Структура проекта

```
app/src/main/java/com/aerohockey/game/
├── AeroHockeyApplication.kt          # Application класс с инициализацией SDK
├── ads/
│   └── AdsManager.kt                  # Менеджер рекламы (баннер, interstitial, rewarded)
├── data/
│   ├── model/
│   │   └── GameStats.kt               # Модель статистики
│   └── repository/
│       └── StatsRepository.kt         # Репозиторий для работы с DataStore
├── domain/
│   ├── engine/
│   │   └── PhysicsEngine.kt           # Физический движок
│   └── model/
│       ├── GameState.kt               # Состояние игры
│       ├── Goal.kt                    # Модель ворот
│       ├── Paddle.kt                  # Модель биты
│       ├── Puck.kt                    # Модель шайбы
│       └── Vector2D.kt                # Вектор для физики
└── ui/
    ├── about/
    │   └── AboutActivity.kt           # Экран "О приложении"
    ├── game/
    │   ├── GameActivity.kt            # Игровой экран
    │   ├── GameRenderer.kt            # Отрисовщик игровых элементов
    │   └── GameView.kt                # Custom View с игровым циклом
    ├── menu/
    │   └── MainActivity.kt            # Главное меню
    ├── result/
    │   └── ResultActivity.kt          # Экран результатов
    ├── settings/
    │   └── SettingsActivity.kt        # Настройки
    ├── splash/
    │   └── SplashActivity.kt          # Splash screen
    └── stats/
        └── StatsActivity.kt           # Статистика
```

## Настройка и запуск

### 1. Требования

- **Android Studio:** 2021.1.1 (Arctic Fox) или выше
- **JDK:** 17
- **Gradle:** 8.0+

### 2. Клонирование проекта

```bash
git clone <repository-url>
cd aerohoskey
```

### 3. Настройка Yandex Mobile Ads SDK

1. Зарегистрируйтесь в [Yandex РСЯ](https://partner.yandex.ru/) или [Adfox](https://www.adfox.ru/)
2. Создайте рекламные блоки для:
   - **Баннера** (адаптивный sticky-баннер)
   - **Interstitial** (полноэкранная реклама)
   - **Rewarded** (реклама с вознаграждением)
3. Откройте файл `app/src/main/java/com/aerohockey/game/ads/AdsManager.kt`
4. Замените тестовые ID на реальные:

```kotlin
companion object {
    private const val BANNER_AD_UNIT_ID = "ВАШ_BANNER_ID"
    private const val INTERSTITIAL_AD_UNIT_ID = "ВАШ_INTERSTITIAL_ID"
    private const val REWARDED_AD_UNIT_ID = "ВАШ_REWARDED_ID"
}
```

### 4. Сборка проекта

```bash
./gradlew build
```

### 5. Запуск на устройстве/эмуляторе

```bash
./gradlew installDebug
```

Или через Android Studio: **Run → Run 'app'**

## Проверка интеграции Yandex Ads SDK

После запуска приложения проверьте logcat на наличие сообщений:

```
D/AdsManager: Yandex Mobile Ads SDK успешно инициализирован
D/AdsManager: Баннер загружается...
D/AdsManager: Interstitial загружена
```

Поиск в logcat:
```bash
adb logcat | grep "Yandex"
```

## Игровая механика

### Управление

- **Игрок 1 (красный, внизу):** Касайтесь нижней половины экрана пальцем
- **Игрок 2 (синий, вверху):** Касайтесь верхней половины экрана пальцем
- Двигайте пальцем, чтобы управлять битой
- Цель: забить шайбу в ворота соперника

### Правила

- Первый игрок, набравший **7 голов**, побеждает
- Биты не могут пересекать центральную линию
- После каждого гола — небольшая пауза и сброс шайбы в центр

## Монетизация

### Баннер

- Показывается на главном экране (внизу)
- Sticky-баннер (прилипает к низу экрана)

### Interstitial

- Показывается при переигрывании после завершения матча
- Не показывается слишком часто (UX-friendly)

### Rewarded (будущая реализация)

- Можно добавить функцию "Продолжить игру" после поражения за просмотр рекламы

## Настройки ProGuard

Все правила для минификации находятся в `app/proguard-rules.pro`:

- Исключения для Yandex Ads SDK
- Правила для Kotlin Coroutines
- Правила для DataStore

## Возможные улучшения

- [ ] Добавить звуковые эффекты (удар шайбы, гол)
- [ ] Добавить вибрацию при столкновениях
- [ ] Анимация гола (эффект частиц)
- [ ] Выбор сложности (скорость шайбы)
- [ ] Режим игры с ИИ
- [ ] Онлайн-мультиплеер
- [ ] Таблица лидеров

## Лицензия

MIT License

## Контакты

**Разработчик:** AeroHockey Team
**Email:** support@aerohockey.com

---

**Примечание:** Перед публикацией в Google Play замените все тестовые ID рекламных блоков на реальные из вашего кабинета Yandex РСЯ/Adfox.
