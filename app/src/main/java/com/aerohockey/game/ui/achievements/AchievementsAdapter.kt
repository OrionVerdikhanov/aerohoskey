package com.aerohockey.game.ui.achievements

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aerohockey.game.R
import com.aerohockey.game.databinding.ItemAchievementBinding
import com.aerohockey.game.domain.model.Achievement

/**
 * Адаптер для списка достижений
 */
class AchievementsAdapter : ListAdapter<Achievement, AchievementsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAchievementBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemAchievementBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(achievement: Achievement) {
            val context = binding.root.context

            // Иконка
            binding.tvIcon.text = achievement.icon

            // Заголовок
            binding.tvTitle.text = context.getString(achievement.titleResId)

            // Описание
            binding.tvDescription.text = context.getString(achievement.descriptionResId)

            // Статус (разблокировано/заблокировано)
            if (achievement.isUnlocked) {
                binding.tvStatus.text = "✅"
                binding.iconContainer.alpha = 1.0f
            } else {
                binding.tvStatus.text = "🔒"
                binding.iconContainer.alpha = 0.5f
            }

            // Прогресс
            val progressPercent = achievement.getProgressPercent()
            binding.progressBar.progress = progressPercent

            // Цвет прогресс бара
            binding.progressBar.setIndicatorColor(achievement.color)

            // Текст прогресса
            if (achievement.maxProgress > 1) {
                binding.tvProgress.text = context.getString(
                    R.string.achievements_progress,
                    achievement.progress,
                    achievement.maxProgress
                )
            } else {
                binding.tvProgress.text = if (achievement.isUnlocked) {
                    context.getString(R.string.achievements_unlocked)
                } else {
                    context.getString(R.string.achievements_locked)
                }
            }

            // Цвет фона иконки
            binding.iconContainer.background.setColorFilter(
                achievement.color,
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}
