package org.example.app.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GameEngineTest {

    @Test
    fun `no winner on empty board and not draw`() {
        val state = GameEngine.reset()
        assertNull(GameEngine.getWinner(state.board))
        assertEquals(false, GameEngine.isDraw(state.board))
    }

    @Test
    fun `X wins on first row`() {
        var s = GameEngine.reset()
        s = GameEngine.makeMove(s, 0) // X
        s = GameEngine.makeMove(s, 3) // O
        s = GameEngine.makeMove(s, 1) // X
        s = GameEngine.makeMove(s, 4) // O
        s = GameEngine.makeMove(s, 2) // X wins
        assertEquals(true, s.gameOver)
        assertEquals('X', s.winner)
    }

    @Test
    fun `O wins on diagonal`() {
        var s = GameEngine.reset()
        s = GameEngine.makeMove(s, 0) // X
        s = GameEngine.makeMove(s, 2) // O
        s = GameEngine.makeMove(s, 1) // X
        s = GameEngine.makeMove(s, 4) // O
        s = GameEngine.makeMove(s, 3) // X
        s = GameEngine.makeMove(s, 6) // O wins (2,4,6)
        assertEquals(true, s.gameOver)
        assertEquals('O', s.winner)
    }

    @Test
    fun `detect draw`() {
        // Board: X O X
        //        X O O
        //        O X X
        var s = GameEngine.reset()
        s = GameEngine.makeMove(s, 0) // X
        s = GameEngine.makeMove(s, 1) // O
        s = GameEngine.makeMove(s, 2) // X
        s = GameEngine.makeMove(s, 4) // O
        s = GameEngine.makeMove(s, 3) // X
        s = GameEngine.makeMove(s, 5) // O
        s = GameEngine.makeMove(s, 7) // X
        s = GameEngine.makeMove(s, 6) // O
        s = GameEngine.makeMove(s, 8) // X
        assertEquals(true, s.gameOver)
        assertNull(s.winner)
    }
}
