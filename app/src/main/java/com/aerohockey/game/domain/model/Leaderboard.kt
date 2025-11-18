package com.aerohockey.game.domain.model

/**
 * Запись в таблице лидеров
 */
data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val score: Int,
    val date: Long,
    val recordType: RecordType
) {
    companion object {
        /**
         * Создать запись
         */
        fun create(
            score: Int,
            recordType: RecordType,
            playerName: String = "Игрок"
        ): LeaderboardEntry {
            return LeaderboardEntry(
                rank = 0,
                playerName = playerName,
                score = score,
                date = System.currentTimeMillis(),
                recordType = recordType
            )
        }
    }
}

/**
 * Тип рекорда
 */
enum class RecordType(val titleResId: Int, val icon: String) {
    HIGHEST_COMBO(0, "🔥"),
    FASTEST_WIN(0, "⚡"),
    MOST_GOALS_MATCH(0, "⚽"),
    LONGEST_MATCH(0, "⏱️"),
    MOST_POWERUPS(0, "✨")
}

/**
 * Таблица лидеров
 */
data class Leaderboard(
    val entries: List<LeaderboardEntry>
) {
    /**
     * Добавить запись
     */
    fun addEntry(entry: LeaderboardEntry): Leaderboard {
        val newEntries = (entries + entry)
            .sortedByDescending { it.score }
            .take(10) // Топ 10
            .mapIndexed { index, e -> e.copy(rank = index + 1) }

        return copy(entries = newEntries)
    }

    /**
     * Проверить, является ли рекордом
     */
    fun isNewRecord(score: Int, recordType: RecordType): Boolean {
        val typeEntries = entries.filter { it.recordType == recordType }
        return typeEntries.isEmpty() || score > typeEntries.maxOf { it.score }
    }

    /**
     * Получить лучший результат
     */
    fun getBestScore(recordType: RecordType): Int? {
        return entries
            .filter { it.recordType == recordType }
            .maxOfOrNull { it.score }
    }
}
