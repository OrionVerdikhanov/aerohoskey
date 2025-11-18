package com.aerohockey.game.ui.quests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aerohockey.game.R
import com.aerohockey.game.data.repository.PlayerProgressRepository
import com.aerohockey.game.databinding.ActivityQuestsBinding
import com.aerohockey.game.domain.progression.DailyQuest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Экран ежедневных заданий
 */
class QuestsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestsBinding
    private lateinit var progressRepository: PlayerProgressRepository
    private lateinit var questsAdapter: QuestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressRepository = PlayerProgressRepository(this)

        setupToolbar()
        setupRecyclerView()
        loadQuests()
        startTimerUpdate()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        questsAdapter = QuestsAdapter(
            onClaimClick = { quest -> claimReward(quest) }
        )

        binding.rvQuests.apply {
            layoutManager = LinearLayoutManager(this@QuestsActivity)
            adapter = questsAdapter
        }
    }

    private fun loadQuests() {
        lifecycleScope.launch {
            progressRepository.getDailyQuests().collect { quests ->
                questsAdapter.submitList(quests)

                // Проверяем, есть ли квесты
                if (quests.isEmpty()) {
                    // Генерируем новые квесты если их нет
                    val newQuests = DailyQuest.createDailySet(3)
                    newQuests.forEach { quest ->
                        progressRepository.saveDailyQuest(quest)
                    }
                }
            }
        }
    }

    private fun startTimerUpdate() {
        lifecycleScope.launch {
            while (isActive) {
                updateTimeRemaining()
                delay(1000) // Обновляем каждую секунду
            }
        }
    }

    private fun updateTimeRemaining() {
        lifecycleScope.launch {
            val quests = progressRepository.getDailyQuests().first()
            if (quests.isNotEmpty()) {
                val firstQuest = quests.first()
                val timeLeft = firstQuest.expiresAt - System.currentTimeMillis()

                if (timeLeft > 0) {
                    val hours = TimeUnit.MILLISECONDS.toHours(timeLeft)
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % 60

                    binding.tvTimeRemaining.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                } else {
                    // Квесты истекли, создаем новые
                    binding.tvTimeRemaining.text = "00:00:00"
                    resetQuests()
                }
            }
        }
    }

    private fun resetQuests() {
        lifecycleScope.launch {
            // Очищаем старые квесты
            val currentQuests = progressRepository.getDailyQuests().first()
            currentQuests.forEach { quest ->
                progressRepository.deleteDailyQuest(quest.id)
            }

            // Создаем новые
            val newQuests = DailyQuest.createDailySet(3)
            newQuests.forEach { quest ->
                progressRepository.saveDailyQuest(quest)
            }

            Snackbar.make(binding.root, "🎉 Новые задания доступны!", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun claimReward(quest: DailyQuest) {
        if (!quest.isCompleted) {
            Snackbar.make(binding.root, "Задание еще не выполнено", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (quest.isClaimed) {
            Snackbar.make(binding.root, "Награда уже получена", Snackbar.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Начисляем награды
            progressRepository.addXP(quest.reward.xp)
            progressRepository.addCoins(quest.reward.coins)

            // Отмечаем квест как полученный
            val updatedQuest = quest.copy(isClaimed = true)
            progressRepository.saveDailyQuest(updatedQuest)

            Snackbar.make(
                binding.root,
                "🎁 Получено: ⭐${quest.reward.xp} XP, 💰${quest.reward.coins} монет",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
