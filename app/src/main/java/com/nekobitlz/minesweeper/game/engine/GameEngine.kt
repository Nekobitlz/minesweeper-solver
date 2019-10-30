package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.GameState
import com.nekobitlz.minesweeper.game.managers.CacheManager.BOMBS_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.COLUMN_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.ROW_COUNT
import com.nekobitlz.minesweeper.game.models.Cell

class GameEngine {

    private var gameState = GameState.NO_STATE;
    private val board = Board(ROW_COUNT, COLUMN_COUNT, BOMBS_COUNT)

    private fun start(x: Int, y: Int) {
        gameState = GameState.PLAYING;
        board.initGame(x, y)
        board.openCells(x, y)
    }

    fun handleShortPress(x: Int, y: Int) {
        when (gameState) {
            GameState.NO_STATE -> {
                start(x, y)
            }
            GameState.PLAYING -> {
                board.openCells(x, y)
            }
            GameState.GAME_OVER -> { /* TODO() */ }
        }
    }

    fun getCells(): Array<Array<Cell>> = board.cells

    fun reset() {
        gameState = GameState.NO_STATE
    }
}