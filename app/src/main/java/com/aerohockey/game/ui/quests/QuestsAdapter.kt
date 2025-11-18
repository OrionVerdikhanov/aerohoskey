package com.aerohockey.game.ui.quests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aerohockey.game.databinding.ItemQuestBinding
import com.aerohockey.game.domain.progression.DailyQuest
import com.aerohockey.game.domain.progression.QuestType

/**
 * Адаптер для отображения ежедневных заданий
 */
class QuestsAdapter(
    private val onClaimClick: (DailyQuest) -> Unit
) : ListAdapter<DailyQuest, QuestsAdapter.QuestViewHolder>(QuestDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val binding = ItemQuestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestViewHolder(binding, onClaimClick)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class QuestViewHolder(
        private val binding: ItemQuestBinding,
        private val onClaimClick: (DailyQuest) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quest: DailyQuest) {
            binding.apply {
                // Иконка квеста
                tvQuestIcon.text = when (quest.type) {
                    QuestType.SCORE_GOALS -> "⚽"
                    QuestType.WIN_MATCHES -> "🏆"
                    QuestType.COLLECT_POWERUPS -> "⚡"
                    QuestType.GET_COMBO -> "🔥"
                    QuestType.PLAY_MATCHES -> "🎮"
                }

                // Название квеста
                tvQuestTitle.text = when (quest.type) {
                    QuestType.SCORE_GOALS -> "Забить ${quest.target} голов"
                    QuestType.WIN_MATCHES -> "Выиграть ${quest.target} матчей"
                    QuestType.COLLECT_POWERUPS -> "Собрать ${quest.target} бонусов"
                    QuestType.GET_COMBO -> "Сделать комбо x${quest.target}"
                    QuestType.PLAY_MATCHES -> "Сыграть ${quest.target} матчей"
                }

                // Прогресс
                val progress = quest.progress.coerceAtMost(quest.target)
                tvQuestProgress.text = "$progress/${quest.target}"

                // Прогресс бар
                progressQuest.max = quest.target
                progressQuest.progress = progress

                // Награды
                tvRewardXP.text = "${quest.reward.xp} XP"
                tvRewardCoins.text = quest.reward.coins.toString()

                // Кнопка получения награды
                btnClaim.isEnabled = quest.isCompleted && !quest.isClaimed
                btnClaim.text = when {
                    quest.isClaimed -> "✓"
                    quest.isCompleted -> "Забрать"
                    else -> "${progress}/${quest.target}"
                }

                btnClaim.setOnClickListener {
                    if (quest.isCompleted && !quest.isClaimed) {
                        onClaimClick(quest)
                    }
                }

                // Изменяем внешний вид если выполнено
                root.alpha = if (quest.isClaimed) 0.6f else 1f
            }
        }
    }

    class QuestDiffCallback : DiffUtil.ItemCallback<DailyQuest>() {
        override fun areItemsTheSame(oldItem: DailyQuest, newItem: DailyQuest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DailyQuest, newItem: DailyQuest): Boolean {
            return oldItem == newItem
        }
    }
}
