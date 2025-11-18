package com.aerohockey.game.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aerohockey.game.databinding.ItemLeaderboardEntryBinding
import com.aerohockey.game.domain.progression.LeaderboardEntry

/**
 * Адаптер для отображения таблицы лидеров
 */
class LeaderboardAdapter : ListAdapter<LeaderboardEntry, LeaderboardAdapter.LeaderboardViewHolder>(LeaderboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LeaderboardViewHolder(
        private val binding: ItemLeaderboardEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: LeaderboardEntry) {
            binding.apply {
                tvRank.text = entry.rank.toString()
                tvPlayerName.text = entry.playerName
                tvScore.text = entry.score.toString()
            }
        }
    }

    class LeaderboardDiffCallback : DiffUtil.ItemCallback<LeaderboardEntry>() {
        override fun areItemsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem.rank == newItem.rank && oldItem.playerName == newItem.playerName
        }

        override fun areContentsTheSame(oldItem: LeaderboardEntry, newItem: LeaderboardEntry): Boolean {
            return oldItem == newItem
        }
    }
}
