package com.aerohockey.game.ui.achievements

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aerohockey.game.data.repository.AchievementsRepository
import com.aerohockey.game.databinding.ActivityAchievementsBinding
import kotlinx.coroutines.launch

/**
 * Экран достижений с красивым UI
 */
class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private lateinit var repository: AchievementsRepository
    private lateinit var adapter: AchievementsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация
        repository = AchievementsRepository(this)
        setupToolbar()
        setupRecyclerView()
        loadAchievements()
    }

    /**
     * Настройка toolbar
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Настройка RecyclerView
     */
    private fun setupRecyclerView() {
        adapter = AchievementsAdapter()
        binding.rvAchievements.layoutManager = LinearLayoutManager(this)
        binding.rvAchievements.adapter = adapter
    }

    /**
     * Загрузка достижений
     */
    private fun loadAchievements() {
        lifecycleScope.launch {
            repository.getAchievements().collect { achievements ->
                adapter.submitList(achievements)
            }
        }
    }
}
