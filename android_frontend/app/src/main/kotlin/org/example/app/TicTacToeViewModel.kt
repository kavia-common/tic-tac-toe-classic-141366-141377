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
            outcomeShown = savedStateHandle.get<Boolean>(KEY_OUTCOME_SHOWN) ?: false,
            winnerLine = state.winnerLine?.copyOf()
        )

    // PUBLIC_INTERFACE
    fun onCellTapped(index: Int) {
        state = GameEngine.makeMove(state, index)
        persistState()
    }

    // PUBLIC_INTERFACE
    fun onRestart() {
        state = GameEngine.reset()
        savedStateHandle[KEY_OUTCOME_SHOWN] = false
        persistState()
    }

    // PUBLIC_INTERFACE
    fun markOutcomeShown() {
        savedStateHandle[KEY_OUTCOME_SHOWN] = true
    }

    private fun persistState() {
        savedStateHandle[KEY_BOARD] = String(state.board)
        savedStateHandle[KEY_PLAYER] = state.currentPlayer.toString()
        savedStateHandle[KEY_OVER] = state.gameOver
        savedStateHandle[KEY_WINNER] = state.winner?.toString()
        savedStateHandle[KEY_WIN_LINE] = state.winnerLine?.joinToString(",")
    }

    private fun restoreState(): GameState {
        val boardStr = savedStateHandle.get<String>(KEY_BOARD)
        val playerStr = savedStateHandle.get<String>(KEY_PLAYER)
        val over = savedStateHandle.get<Boolean>(KEY_OVER) ?: false
        val winnerStr = savedStateHandle.get<String>(KEY_WINNER)
        val winLineStr = savedStateHandle.get<String>(KEY_WIN_LINE)

        val winnerLine = winLineStr?.takeIf { it.isNotBlank() }?.split(",")?.mapNotNull {
            it.toIntOrNull()
        }?.toIntArray()

        return if (boardStr != null && boardStr.length == 9 && playerStr != null) {
            GameState(
                board = boardStr.toCharArray(),
                currentPlayer = playerStr.first(),
                gameOver = over,
                winner = winnerStr?.firstOrNull(),
                winnerLine = winnerLine
            )
        } else {
            GameEngine.reset()
        }
    }
}
