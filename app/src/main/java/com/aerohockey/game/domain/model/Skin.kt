package com.aerohockey.game.domain.model

/**
 * Скин для шайбы или биты
 */
data class Skin(
    val id: String,
    val name: String,
    val type: SkinType,
    val rarity: SkinRarity,
    val price: Int,
    val color: Int,
    val icon: String,
    var isUnlocked: Boolean = false,
    var isEquipped: Boolean = false,
    val unlockLevel: Int = 1
) {
    companion object {
        /**
         * Получить все доступные скины
         */
        fun getAllSkins(): List<Skin> {
            return listOf(
                // Скины для шайбы
                Skin("puck_default", "Классическая", SkinType.PUCK, SkinRarity.COMMON, 0, 0xFFFFD700.toInt(), "🟡", true, true, 1),
                Skin("puck_red", "Красная молния", SkinType.PUCK, SkinRarity.COMMON, 100, 0xFFFF4444.toInt(), "🔴", false, false, 1),
                Skin("puck_blue", "Синий лед", SkinType.PUCK, SkinRarity.COMMON, 100, 0xFF4444FF.toInt(), "🔵", false, false, 1),
                Skin("puck_green", "Зеленый метеор", SkinType.PUCK, SkinRarity.RARE, 250, 0xFF44FF44.toInt(), "🟢", false, false, 5),
                Skin("puck_purple", "Фиолетовая комета", SkinType.PUCK, SkinRarity.RARE, 250, 0xFFAA44FF.toInt(), "🟣", false, false, 10),
                Skin("puck_rainbow", "Радужная", SkinType.PUCK, SkinRarity.EPIC, 500, 0xFFFF00FF.toInt(), "🌈", false, false, 20),
                Skin("puck_fire", "Огненная", SkinType.PUCK, SkinRarity.EPIC, 500, 0xFFFF6600.toInt(), "🔥", false, false, 30),
                Skin("puck_ice", "Ледяная", SkinType.PUCK, SkinRarity.LEGENDARY, 1000, 0xFF00FFFF.toInt(), "❄️", false, false, 50),

                // Скины для бит
                Skin("paddle_default", "Стандартная", SkinType.PADDLE, SkinRarity.COMMON, 0, 0xFF00FF00.toInt(), "🟢", true, true, 1),
                Skin("paddle_metal", "Металлическая", SkinType.PADDLE, SkinRarity.COMMON, 150, 0xFFCCCCCC.toInt(), "⚙️", false, false, 1),
                Skin("paddle_wood", "Деревянная", SkinType.PADDLE, SkinRarity.COMMON, 150, 0xFF8B4513.toInt(), "🪵", false, false, 1),
                Skin("paddle_gold", "Золотая", SkinType.PADDLE, SkinRarity.RARE, 300, 0xFFFFD700.toInt(), "✨", false, false, 10),
                Skin("paddle_diamond", "Алмазная", SkinType.PADDLE, SkinRarity.EPIC, 600, 0xFF00FFFF.toInt(), "💎", false, false, 25),
                Skin("paddle_rainbow", "Радужная", SkinType.PADDLE, SkinRarity.LEGENDARY, 1200, 0xFFFF00FF.toInt(), "🌈", false, false, 50)
            )
        }

        /**
         * Получить скины по типу
         */
        fun getSkinsByType(type: SkinType): List<Skin> {
            return getAllSkins().filter { it.type == type }
        }
    }
}

/**
 * Тип скина
 */
enum class SkinType {
    PUCK,
    PADDLE
}

/**
 * Редкость скина
 */
enum class SkinRarity(val color: Int, val icon: String) {
    COMMON(0xFFCCCCCC.toInt(), "⚪"),
    RARE(0xFF4444FF.toInt(), "🔵"),
    EPIC(0xFFAA44FF.toInt(), "🟣"),
    LEGENDARY(0xFFFFAA00.toInt(), "🟡")
}
