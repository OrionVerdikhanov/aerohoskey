package com.aerohockey.game.ui.mode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aerohockey.game.databinding.ActivityModeSelectionBinding
import com.aerohockey.game.domain.model.Difficulty
import com.aerohockey.game.domain.model.GameMode
import com.aerohockey.game.ui.game.GameActivity

/**
 * Экран выбора режима игры
 */
class ModeSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModeSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Режим для двух игроков
        binding.cardTwoPlayers.setOnClickListener {
            startGameWithMode(GameMode.TwoPlayers)
        }

        // Режим против AI
        binding.cardVsAI.setOnClickListener {
            showDifficultyDialog()
        }
    }

    /**
     * Показать диалог выбора сложности
     */
    private fun showDifficultyDialog() {
        DifficultyDialog.show(this) { difficulty ->
            startGameWithMode(GameMode.VsAI(difficulty))
        }
    }

    /**
     * Запустить игру с выбранным режимом
     */
    private fun startGameWithMode(mode: GameMode) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GameActivity.EXTRA_GAME_MODE, mode.toSerializable())
        }
        startActivity(intent)
    }

    /**
     * Преобразование GameMode в Serializable для Intent
     */
    private fun GameMode.toSerializable(): String {
        return when (this) {
            is GameMode.TwoPlayers -> "TWO_PLAYERS"
            is GameMode.VsAI -> "VS_AI_${this.difficulty.ordinal}"
            is GameMode.Tournament -> "TOURNAMENT_${this.maxWins}"
        }
    }
}
