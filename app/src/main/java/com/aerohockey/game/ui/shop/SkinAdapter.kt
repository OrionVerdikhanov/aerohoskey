package com.aerohockey.game.ui.shop

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aerohockey.game.databinding.ItemSkinBinding
import com.aerohockey.game.domain.progression.Skin
import com.aerohockey.game.domain.progression.SkinRarity

/**
 * Адаптер для отображения скинов в магазине
 */
class SkinAdapter(
    private val onSkinClick: (Skin) -> Unit
) : ListAdapter<Skin, SkinAdapter.SkinViewHolder>(SkinDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkinViewHolder {
        val binding = ItemSkinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SkinViewHolder(binding, onSkinClick)
    }

    override fun onBindViewHolder(holder: SkinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SkinViewHolder(
        private val binding: ItemSkinBinding,
        private val onSkinClick: (Skin) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(skin: Skin) {
            binding.apply {
                // Название
                tvSkinName.text = skin.name

                // Иконка
                tvSkinIcon.text = skin.icon

                // Цвет иконки
                tvSkinIcon.setTextColor(skin.color)

                // Иконка редкости
                tvRarityIcon.text = when (skin.rarity) {
                    SkinRarity.COMMON -> "⚪"
                    SkinRarity.RARE -> "🔵"
                    SkinRarity.EPIC -> "🟣"
                    SkinRarity.LEGENDARY -> "🟡"
                }

                // Состояние
                when {
                    skin.isEquipped -> {
                        // Экипирован
                        layoutLocked.visibility = View.GONE
                        tvEquipped.visibility = View.VISIBLE
                        root.alpha = 1f
                    }
                    skin.isUnlocked -> {
                        // Разблокирован, но не экипирован
                        layoutLocked.visibility = View.GONE
                        tvEquipped.visibility = View.GONE
                        root.alpha = 1f
                    }
                    else -> {
                        // Заблокирован
                        layoutLocked.visibility = View.VISIBLE
                        tvEquipped.visibility = View.GONE
                        root.alpha = 0.6f

                        // Цена
                        tvPrice.text = skin.price.toString()

                        // Требуемый уровень
                        if (skin.unlockLevel > 1) {
                            tvUnlockLevel.visibility = View.VISIBLE
                            tvUnlockLevel.text = "Уровень ${skin.unlockLevel}"
                        } else {
                            tvUnlockLevel.visibility = View.GONE
                        }
                    }
                }

                // Обработка клика
                root.setOnClickListener {
                    onSkinClick(skin)
                }
            }
        }
    }

    class SkinDiffCallback : DiffUtil.ItemCallback<Skin>() {
        override fun areItemsTheSame(oldItem: Skin, newItem: Skin): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Skin, newItem: Skin): Boolean {
            return oldItem == newItem
        }
    }
}
