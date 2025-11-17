package org.example.app

import android.os.Bundle
import android.view.View
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
        // For AppCompat Toolbar with ComponentActivity, use setActionBar(android.widget.Toolbar) is not compatible.
        // Simply leave the title set, or if needed, we could switch to AppCompatActivity. Keeping simple here.

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
        renderBoard(cellButtons)
        renderStatus(statusText)

        // Set click listeners for cells
        cellButtons.forEachIndexed { index, button ->
            button.contentDescription = getString(R.string.cell_content_description, index)
            button.setOnClickListener {
                viewModel.onCellTapped(index)
                renderBoard(cellButtons)
                renderStatus(statusText)
                maybeShowOutcome(root)
            }
        }

        restartBtn.setOnClickListener {
            viewModel.onRestart()
            renderBoard(cellButtons)
            renderStatus(statusText)
            Snackbar.make(root, getString(R.string.board_cleared), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun renderBoard(cellButtons: List<Button>) {
        val symbols = viewModel.uiState.board
        val enabled = !viewModel.uiState.gameOver
        symbols.forEachIndexed { idx, ch ->
            val btn = cellButtons[idx]
            btn.text = ch.toString()
            btn.isEnabled = enabled && ch == ' '
        }
    }

    private fun renderStatus(tv: TextView) {
        val state = viewModel.uiState
        if (!state.gameOver) {
            tv.text = getString(R.string.turn_label, state.currentPlayer.toString())
            val color =
                if (state.currentPlayer == 'X') ContextCompat.getColor(this, R.color.primary)
                else ContextCompat.getColor(this, R.color.secondary)
            tv.setTextColor(color)
        } else {
            when {
                state.winner == 'X' || state.winner == 'O' -> {
                    tv.text = getString(R.string.winner_label, state.winner.toString())
                    tv.setTextColor(ContextCompat.getColor(this, R.color.secondary))
                }
                else -> {
                    tv.text = getString(R.string.draw_label)
                    tv.setTextColor(ContextCompat.getColor(this, R.color.textPrimary))
                }
            }
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
