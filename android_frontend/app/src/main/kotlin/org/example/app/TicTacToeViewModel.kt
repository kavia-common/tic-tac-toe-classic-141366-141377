package org.example.app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import org.example.app.game.GameEngine
import org.example.app.game.GameState

/**
 * PUBLIC_INTERFACE
 * TicTacToeViewModel manages UI state for the game, persists it across configuration changes,
 * and exposes actions to mutate state via the GameEngine.
 */
class TicTacToeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    data class UiState(
        val board: CharArray,
        val currentPlayer: Char,
        val gameOver: Boolean,
        val winner: Char?,
        val outcomeShown: Boolean,
        val winnerLine: IntArray?
    )

    companion object Keys {
        private const val KEY_BOARD = "board"
        private const val KEY_PLAYER = "player"
        private const val KEY_OVER = "over"
        private const val KEY_WINNER = "winner"
        private const val KEY_WIN_LINE = "winner_line"
        private const val KEY_OUTCOME_SHOWN = "outcome_shown"
    }

    private var state: GameState = restoreState()

    val uiState: UiState
        get() = UiState(
            board = state.board.copyOf(),
            currentPlayer = state.currentPlayer,
            gameOver = state.gameOver,
            winner = state.winner,
            // outcomeShown is persisted independently in SavedStateHandle so dialogs are not reshown after process death.
            outcomeShown = savedStateHandle.get<Boolean>(KEY_OUTCOME_SHOWN) ?: false,
            // winnerLine is stored in GameState but we also persist a copy in SavedStateHandle to restore highlighting after process death
            winnerLine = state.winnerLine?.copyOf()
        )

    // PUBLIC_INTERFACE
    fun onCellTapped(index: Int) {
        state = GameEngine.makeMove(state, index)
        // If game just ended, ensure outcomeShown defaults to false for the new outcome (so dialog can appear)
        if (state.gameOver) {
            // Only set default if not already set by a previous restore/flow
            if (savedStateHandle.get<Boolean>(KEY_OUTCOME_SHOWN) == null) {
                savedStateHandle[KEY_OUTCOME_SHOWN] = false
            }
        }
        persistState()
    }

    // PUBLIC_INTERFACE
    fun onRestart() {
        state = GameEngine.reset()
        // Reset flags on new game
        savedStateHandle[KEY_OUTCOME_SHOWN] = false
        persistState()
    }

    // PUBLIC_INTERFACE
    fun markOutcomeShown() {
        savedStateHandle[KEY_OUTCOME_SHOWN] = true
    }

    private fun persistState() {
        // Persist core game state
        savedStateHandle[KEY_BOARD] = String(state.board)
        savedStateHandle[KEY_PLAYER] = state.currentPlayer.toString()
        savedStateHandle[KEY_OVER] = state.gameOver
        savedStateHandle[KEY_WINNER] = state.winner?.toString()
        // Persist winnerLine and outcomeShown to survive process death and allow UI to re-highlight/avoid re-showing dialog
        savedStateHandle[KEY_WIN_LINE] = state.winnerLine?.joinToString(",")
        // Ensure outcomeShown has a default value for first run
        if (savedStateHandle.get<Boolean>(KEY_OUTCOME_SHOWN) == null) {
            savedStateHandle[KEY_OUTCOME_SHOWN] = false
        }
    }

    private fun restoreState(): GameState {
        val boardStr = savedStateHandle.get<String>(KEY_BOARD)
        val playerStr = savedStateHandle.get<String>(KEY_PLAYER)
        val over = savedStateHandle.get<Boolean>(KEY_OVER) ?: false
        val winnerStr = savedStateHandle.get<String>(KEY_WINNER)
        val winLineStr = savedStateHandle.get<String>(KEY_WIN_LINE)

        // Ensure defaults are present on first run
        if (savedStateHandle.get<Boolean>(KEY_OUTCOME_SHOWN) == null) {
            savedStateHandle[KEY_OUTCOME_SHOWN] = false
        }

        val persistedWinnerLine = winLineStr
            ?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?.toIntArray()

        return if (boardStr != null && boardStr.length == 9 && playerStr != null) {
            GameState(
                board = boardStr.toCharArray(),
                currentPlayer = playerStr.first(),
                gameOver = over,
                winner = winnerStr?.firstOrNull(),
                winnerLine = persistedWinnerLine
            )
        } else {
            // First run / nothing to restore
            GameEngine.reset()
        }
    }
}
