package org.example.app.game

/**
 * Represents the immutable snapshot of the game board.
 * Board is a CharArray of size 9 where values are 'X', 'O', or ' ' (space for empty).
 */
data class GameState(
    val board: CharArray = CharArray(9) { ' ' },
    val currentPlayer: Char = 'X',
    val gameOver: Boolean = false,
    val winner: Char? = null,
    // Indices of the winning line if there is a winner; null otherwise
    val winnerLine: IntArray? = null
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

        val winnerLine = getWinnerLine(newBoard)
        val winner = winnerLine?.let { newBoard[it[0]] }
        val isDraw = winner == null && isDraw(newBoard)

        return if (winner != null) {
            state.copy(board = newBoard, gameOver = true, winner = winner, winnerLine = winnerLine)
        } else if (isDraw) {
            state.copy(board = newBoard, gameOver = true, winner = null, winnerLine = null)
        } else {
            val nextPlayer = if (state.currentPlayer == 'X') 'O' else 'X'
            state.copy(board = newBoard, currentPlayer = nextPlayer, winnerLine = null)
        }
    }

    // PUBLIC_INTERFACE
    fun getWinner(board: CharArray): Char? {
        val line = getWinnerLine(board)
        return line?.let { board[it[0]] }
    }

    // PUBLIC_INTERFACE
    fun getWinnerLine(board: CharArray): IntArray? {
        for (line in WIN_LINES) {
            val a = board[line[0]]
            val b = board[line[1]]
            val c = board[line[2]]
            if (a != ' ' && a == b && b == c) {
                return line
            }
        }
        return null
    }

    // PUBLIC_INTERFACE
    fun isDraw(board: CharArray): Boolean {
        return board.all { it == 'X' || it == 'O' }
    }
}
