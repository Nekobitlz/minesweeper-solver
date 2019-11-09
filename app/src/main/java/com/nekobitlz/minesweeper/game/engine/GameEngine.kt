package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.GameState.*
import com.nekobitlz.minesweeper.game.managers.CacheManager.BOMBS_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.COLUMN_COUNT
import com.nekobitlz.minesweeper.game.managers.CacheManager.ROW_COUNT
import com.nekobitlz.minesweeper.game.models.Cell

open class GameEngine {

    internal var gameState = NO_STATE
    internal var board = Board(COLUMN_COUNT, ROW_COUNT, BOMBS_COUNT)

    internal fun handleShortPress(x: Int, y: Int) {
        when (gameState) {
            NO_STATE -> {
                start(x, y)
                if (board.openedAllExceptBombs()) winGame()
            }
            PLAYING -> {
                board.openCells(x, y)

                when {
                    board.isFullyOpen() -> gameOver()
                    board.openedAllExceptBombs() -> winGame()
                }
            }
            else -> { /* nothing */ }
        }
    }

    internal fun handleLongPress(x: Int, y: Int) = when (gameState) {
        NO_STATE -> start(x, y)
        PLAYING -> board.handleFlag(x, y)
        else -> { /* nothing */ }
    }

    internal fun reset() {
        gameState = NO_STATE
        board.reset()
    }

    internal fun getCells(): Array<Array<Cell>> = board.cells

    protected open fun start(x: Int, y: Int) {
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

    internal fun isFinished(): Boolean = gameState == GAME_OVER || gameState == WIN
}