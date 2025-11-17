package com.aerohockey.game.ui.mode

import android.app.AlertDialog
import android.content.Context
import com.aerohockey.game.R
import com.aerohockey.game.domain.model.Difficulty

/**
 * Диалог выбора уровня сложности
 */
object DifficultyDialog {

    /**
     * Показать диалог выбора сложности
     */
    fun show(context: Context, onSelected: (Difficulty) -> Unit) {
        val difficulties = arrayOf(
            context.getString(R.string.difficulty_easy),
            context.getString(R.string.difficulty_medium),
            context.getString(R.string.difficulty_hard),
            context.getString(R.string.difficulty_extreme)
        )

        AlertDialog.Builder(context)
            .setTitle(R.string.difficulty_dialog_title)
            .setItems(difficulties) { dialog, which ->
                val selected = Difficulty.fromOrdinal(which)
                onSelected(selected)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
