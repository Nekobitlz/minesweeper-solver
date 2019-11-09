package com.nekobitlz.minesweeper

import com.nekobitlz.minesweeper.game.engine.Board
import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.enums.CellState
import com.nekobitlz.minesweeper.game.enums.GameState.*
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.solver.Solver
import junit.framework.TestCase.assertEquals
import org.junit.Test

class EngineTest {

    @Test
    fun handleShortPress() {
        val gameEngine = GameEngine()
        assertEquals(NO_STATE, gameEngine.gameState)

        gameEngine.board = Board(9, 9, 20)
        gameEngine.handleShortPress(0, 0)
        assertEquals(PLAYING, gameEngine.gameState)

        val bomb = findBombAtBoard(gameEngine.board)
        gameEngine.handleShortPress(bomb.x, bomb.y)
        assertEquals(GAME_OVER, gameEngine.gameState)

        gameEngine.reset()
        assertEquals(NO_STATE, gameEngine.gameState)

        gameEngine.board = Board(9, 9, 1)

        val solver = Solver()
        solver.initSolver(gameEngine, 9, 9)
        solver.solve()

        assertEquals(WIN, gameEngine.gameState)
    }

    private fun findBombAtBoard(board: Board): Cell {
        board.cells.forEach { it.forEach { cell -> if (cell.cellType.isBomb()) return cell } }

        return Cell(0, 0) // should never happen
    }

    @Test
    fun handleLongPress() {
        val gameEngine = GameEngine()
        assertEquals(NO_STATE, gameEngine.gameState)

        gameEngine.board = Board(9, 9, 30)
        gameEngine.handleLongPress(0, 0)
        assertEquals(PLAYING, gameEngine.gameState)

        val notOpenedCell = findNotOpenedCell(gameEngine.board)
        gameEngine.handleLongPress(notOpenedCell.x, notOpenedCell.y)
        assertEquals(CellState.FLAGGED, gameEngine.getCells()[notOpenedCell.x][notOpenedCell.y].cellState)

        gameEngine.handleLongPress(notOpenedCell.x, notOpenedCell.y)
        assertEquals(CellState.NO_STATE, gameEngine.getCells()[notOpenedCell.x][notOpenedCell.y].cellState)

        val openedCell = findOpenedCell(gameEngine.board)
        assertEquals(CellState.OPENED, gameEngine.getCells()[openedCell.x][openedCell.y].cellState)

        gameEngine.handleLongPress(openedCell.x, openedCell.y)
        assertEquals(CellState.OPENED, gameEngine.getCells()[openedCell.x][openedCell.y].cellState)
    }

    private fun findOpenedCell(board: Board): Cell {
        board.cells.forEach {
            it.forEach { cell -> if (cell.cellState.isOpened()) return cell }
        }

        return Cell(0, 0) // should never happen
    }

    private fun findNotOpenedCell(board: Board): Cell {
        board.cells.forEach {
            it.forEach { cell -> if (cell.cellState == CellState.NO_STATE) return cell }
        }

        return Cell(8, 8) // should never happen
    }
}