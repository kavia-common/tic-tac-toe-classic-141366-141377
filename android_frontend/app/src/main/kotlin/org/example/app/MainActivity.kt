package org.example.app

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

/**
 * PUBLIC_INTERFACE
 * MainActivity is the entrypoint Activity that renders the Tic Tac Toe board,
 * shows the current player's turn, and provides the restart action.
 * It observes a ViewModel (TicTacToeViewModel) for state and handles dialogs/snackbars for outcomes.
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: TicTacToeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TicTacToe_OceanProfessional)
        setContentView(R.layout.activity_main)

        // Obtain ViewModel using ViewModelProvider with SavedState support
        viewModel = ViewModelProvider(this).get(TicTacToeViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        toolbar.title = getString(R.string.app_name)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textPrimary))
        toolbar.contentDescription = getString(R.string.app_name)
        // Using Toolbar directly with ComponentActivity; styling provided by theme (Ocean Professional).

        val statusText = findViewById<TextView>(R.id.statusText)
        val restartBtn = findViewById<Button>(R.id.restartButton)
        val root = findViewById<View>(R.id.root)

        // Setup board cell buttons
        val cellButtons = listOf(
            R.id.cell0, R.id.cell1, R.id.cell2,
            R.id.cell3, R.id.cell4, R.id.cell5,
            R.id.cell6, R.id.cell7, R.id.cell8
        ).map { findViewById<Button>(it) }

        // Render initial state
        renderBoard(cellButtons, animateAppearance = false)
        renderStatus(statusText, animateColor = false)

        // Set click listeners for cells
        cellButtons.forEachIndexed { index, button ->
            button.contentDescription = getString(R.string.cell_content_description, index)
            button.setOnClickListener {
                viewModel.onCellTapped(index)
                renderBoard(cellButtons, animateAppearance = true)
                renderStatus(statusText, animateColor = true)
                maybeShowOutcome(root)
            }
        }

        restartBtn.setOnClickListener {
            viewModel.onRestart()
            renderBoard(cellButtons, animateAppearance = false)
            renderStatus(statusText, animateColor = true)
            Snackbar.make(root, getString(R.string.board_cleared), Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Renders board symbols, applies subtle pop-in animation when a cell changes from empty to a symbol,
     * and highlights the winning line if present.
     */
    private fun renderBoard(cellButtons: List<Button>, animateAppearance: Boolean = true) {
        val ui = viewModel.uiState
        val symbols = ui.board
        val enabled = !ui.gameOver

        // Compute a set for quick highlighting lookup
        val winningSet: Set<Int> = ui.winnerLine?.toSet() ?: emptySet()

        symbols.forEachIndexed { idx, ch ->
            val btn = cellButtons[idx]
            val prevText = btn.text?.toString() ?: ""
            val newText = if (ch == ' ') "" else ch.toString()

            // Update text and enabled state
            btn.text = newText
            btn.isEnabled = enabled && ch == ' '

            // Subtle scale + fade-in if newly placed
            if (animateAppearance && prevText.isEmpty() && newText.isNotEmpty()) {
                btn.scaleX = 0.8f
                btn.scaleY = 0.8f
                btn.alpha = 0f
                btn.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(180)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }

            // Winning highlight styling
            if (winningSet.contains(idx)) {
                // Increase emphasis via text color and slight scale pulse
                btn.setTextColor(ContextCompat.getColor(this, R.color.primary))
                btn.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .setDuration(160)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        btn.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(140)
                            .start()
                    }
                    .start()
            } else {
                // Reset non-winning cells to default text color
                btn.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
            }
        }
    }

    /**
     * Renders the status text and smoothly transitions the color when the turn changes or on outcome.
     */
    private fun renderStatus(tv: TextView, animateColor: Boolean = true) {
        val state = viewModel.uiState
        val targetText: String
        val targetColor: Int

        if (!state.gameOver) {
            targetText = getString(R.string.turn_label, state.currentPlayer.toString())
            targetColor =
                if (state.currentPlayer == 'X') ContextCompat.getColor(this, R.color.primary)
                else ContextCompat.getColor(this, R.color.secondary)
        } else {
            if (state.winner == 'X' || state.winner == 'O') {
                targetText = getString(R.string.winner_label, state.winner.toString())
                targetColor = ContextCompat.getColor(this, R.color.secondary)
            } else {
                targetText = getString(R.string.draw_label)
                targetColor = ContextCompat.getColor(this, R.color.textPrimary)
            }
        }

        // Update text immediately to avoid mismatch during color animation
        tv.text = targetText

        if (animateColor) {
            val currentColor = tv.currentTextColor
            if (currentColor != targetColor) {
                val animator = ObjectAnimator.ofInt(tv, "textColor", currentColor, targetColor)
                animator.duration = 220
                animator.setEvaluator(ArgbEvaluator())
                animator.start()
            }
        } else {
            tv.setTextColor(targetColor)
        }
    }

    private fun maybeShowOutcome(anchor: View) {
        val state = viewModel.uiState
        if (state.gameOver && !state.outcomeShown) {
            val title: String
            val message: String
            if (state.winner == 'X' || state.winner == 'O') {
                title = getString(R.string.game_over)
                message = getString(R.string.winner_message, state.winner.toString())
            } else {
                title = getString(R.string.game_over)
                message = getString(R.string.draw_label)
            }

            // Dialog for outcome
            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.restart) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.onRestart()
                    Snackbar.make(anchor, getString(R.string.board_cleared), Snackbar.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton(R.string.close, null)
                .show()

            viewModel.markOutcomeShown()
        }
    }
}
