package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.GameState.*
import com.nekobitlz.minesweeper.game.managers.CacheManager.BOMBS_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.COLUMN_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.ROW_COUNT
import com.nekobitlz.minesweeper.game.models.Cell

class GameEngine {

    var gameState = NO_STATE

    private val board = Board(ROW_COUNT, COLUMN_COUNT, BOMBS_COUNT)

    fun handleShortPress(x: Int, y: Int) {
        when (gameState) {
            NO_STATE -> start(x, y)
            PLAYING -> {
                board.openCells(x, y)

                when {
                    board.isFullyOpen() -> gameOver()
                    board.closedAllExceptBombs() -> winGame()
                }
            }
            else -> { /* nothing */ }
        }
    }

    fun handleLongPress(x: Int, y: Int) = when (gameState) {
        NO_STATE -> start(x, y)
        PLAYING -> board.handleFlag(x, y)
        else -> { /* nothing */ }
    }

    fun reset() {
        gameState = NO_STATE
    }

    fun getCells(): Array<Array<Cell>> = board.cells

    private fun start(x: Int, y: Int) {
        gameState = PLAYING

        with(board) {
            initGame(x, y)
            openCells(x, y)
        }
    }

    private fun gameOver() {
        gameState = GAME_OVER
    }

    private fun winGame() {
        gameState = WIN
    }
}