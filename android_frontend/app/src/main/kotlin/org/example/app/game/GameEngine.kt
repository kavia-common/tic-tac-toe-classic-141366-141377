package org.example.app.game

/**
 * Represents the immutable snapshot of the game board.
 * Board is a CharArray of size 9 where values are 'X', 'O', or ' ' (space for empty).
 */
data class GameState(
    val board: CharArray = CharArray(9) { ' ' },
    val currentPlayer: Char = 'X',
    val gameOver: Boolean = false,
    val winner: Char? = null
)

/**
 * PUBLIC_INTERFACE
 * GameEngine contains pure game logic for Tic Tac Toe: making moves, winner detection, and draw detection.
 */
object GameEngine {

    private val WIN_LINES = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    // PUBLIC_INTERFACE
    fun reset(): GameState {
        return GameState()
    }

    // PUBLIC_INTERFACE
    fun makeMove(state: GameState, index: Int): GameState {
        if (state.gameOver) return state
        if (index !in 0..8) return state
        if (state.board[index] != ' ') return state

        val newBoard = state.board.copyOf()
        newBoard[index] = state.currentPlayer

        val winner = getWinner(newBoard)
        val isDraw = winner == null && isDraw(newBoard)

        return if (winner != null) {
            state.copy(board = newBoard, gameOver = true, winner = winner)
        } else if (isDraw) {
            state.copy(board = newBoard, gameOver = true, winner = null)
        } else {
            val nextPlayer = if (state.currentPlayer == 'X') 'O' else 'X'
            state.copy(board = newBoard, currentPlayer = nextPlayer)
        }
    }

    // PUBLIC_INTERFACE
    fun getWinner(board: CharArray): Char? {
        for (line in WIN_LINES) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a != ' ' && a == b && b == c) {
                return a
            }
        }
        return null
    }

    // PUBLIC_INTERFACE
    fun isDraw(board: CharArray): Boolean {
        return board.all { it == 'X' || it == 'O' }
    }
}
