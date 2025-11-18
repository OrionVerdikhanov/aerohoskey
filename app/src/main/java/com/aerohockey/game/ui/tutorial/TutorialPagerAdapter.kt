package com.aerohockey.game.ui.tutorial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aerohockey.game.databinding.ItemTutorialSlideBinding

/**
 * Модель слайда туториала
 */
data class TutorialSlide(
    val icon: String,
    val titleResId: Int,
    val descriptionResId: Int
)

/**
 * Адаптер для ViewPager2 с туториалом
 */
class TutorialPagerAdapter(
    private val slides: List<TutorialSlide>
) : RecyclerView.Adapter<TutorialPagerAdapter.SlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemTutorialSlideBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        holder.bind(slides[position])
    }

    override fun getItemCount(): Int = slides.size

    class SlideViewHolder(
        private val binding: ItemTutorialSlideBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(slide: TutorialSlide) {
            binding.tvIcon.text = slide.icon
            binding.tvTitle.text = binding.root.context.getString(slide.titleResId)
            binding.tvDescription.text = binding.root.context.getString(slide.descriptionResId)
        }
    }
}
