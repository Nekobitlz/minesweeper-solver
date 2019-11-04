package com.nekobitlz.minesweeper

import com.nekobitlz.minesweeper.game.engine.Board
import com.nekobitlz.minesweeper.game.enums.CellState
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.models.Cell
import org.junit.Assert.*
import org.junit.Test
import java.lang.Integer.max
import kotlin.random.Random

class BoardTest {

    private lateinit var board: Board
    private lateinit var random: Random
    private var x: Int = 0
    private var y: Int = 0

    private fun initBoard(width: Int, height: Int, bombs: Int) {
        board = Board(width, height, bombs)

        random = Random(max(width, height))
        x = random.nextInt(width)
        y = random.nextInt(height)

        board.initGame(x, y)
    }

    @Test
    fun getCells() {
        getCellsTest(9, 9, 10)
        getCellsTest(10, 10, 91)
        getCellsTest(25, 45, 90)
    }

    private fun getCellsTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        assertEquals(width, board.cells.size)
        assertEquals(height, board.cells[0].size)

        var bombsCount = 0

        board.cells.forEach {
            it.forEach { cell ->
                if (cell.cellType.isBomb()) bombsCount++
            }
        }

        assertEquals(bombs, bombsCount)
    }

    @Test
    fun initGame() {
        initGameTest(9, 9, 10)
        initGameTest(10, 10, 91)
        initGameTest(25, 45, 90)
    }

    private fun initGameTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        var bombsCount = 0
        val cells = board.cells
        val neighbours = Array(width) { IntArray(height) { 0 } }

        // find bombs count & neighbours count
        for (i in 0 until width) {
            for (j in 0 until height) {
                if (cells[i][j].cellType.isBomb()) {
                    bombsCount++
                    setBombsCountForNearestCells(i, j, cells, neighbours)
                }
            }
        }

        assertEquals(bombs, bombsCount)

        for (i in 0 until width) {
            for (j in 0 until height) {
                assertEquals(neighbours[i][j], cells[i][j].bombsNearby)
            }
        }
    }

    private fun setBombsCountForNearestCells(x: Int, y: Int, cells: Array<Array<Cell>>, neighbours: Array<IntArray>) {
        for (nearestX in (x - 1)..(x + 1)) {
            for (nearestY in (y - 1)..(y + 1)) {
                if (coordinatesAreValid(nearestX, nearestY, cells.size, cells[0].size) && cells[nearestX][nearestY] != cells[x][y]) {
                    neighbours[nearestX][nearestY]++
                }
            }
        }
    }

    private fun coordinatesAreValid(x: Int, y: Int, width: Int, height: Int): Boolean = x != -1 && y != -1 && x != width && y != height

    @Test
    fun openCells() {
        openCellsTest(9, 9, 10)
        openCellsTest(10, 10, 91)
        openCellsTest(25, 45, 90)
    }

    private fun openCellsTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        board.cells.forEach { it.forEach { cell -> assertTrue(cell.cellState == CellState.NO_STATE) } }

        x = random.nextInt(width)
        y = random.nextInt(height)

        board.openCells(x, y)

        checkOpenedCells(board.cells, x, y)
    }

    private fun checkOpenedCells(cells: Array<Array<Cell>>, x: Int, y: Int) {
        assertEquals(CellState.OPENED, cells[x][y].cellState)

        when (cells[x][y].cellType) {
            CellType.EMPTY -> checkNearestOpenedCells(cells, x, y)
            CellType.BOMB -> checkAllOpenedCells(cells)
            else -> { /* nothing */ }
        }
    }

    private fun checkAllOpenedCells(cells: Array<Array<Cell>>) {
        cells.forEach { it.forEach { cell -> assertEquals(CellState.OPENED, cell.cellState) } }
    }

    private fun checkNearestOpenedCells(cells: Array<Array<Cell>>, x: Int, y: Int) {
        for (nearestX in (x - 1)..(x + 1)) {
            for (nearestY in (y - 1)..(y + 1)) {
                if (coordinatesAreValid(nearestX, nearestY, cells[0].size, cells.size) &&
                    cells[nearestX][nearestY] != cells[x][y] &&
                    cells[nearestX][nearestY].cellState == CellState.NO_STATE
                ) {
                    checkOpenedCells(cells, nearestX, nearestY)
                }
            }
        }
    }

    @Test
    fun isFullyOpen() {
        isFullyOpenTest(9, 9, 10)
        isFullyOpenTest(10, 10, 91)
        isFullyOpenTest(25, 45, 90)
    }

    private fun isFullyOpenTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        assertFalse(board.isFullyOpen())

        for (i in 0 until ITERATIONS) {
            board.initGame(x, y)

            x = random.nextInt(width)
            y = random.nextInt(height)

            board.openCells(x, y)

            if (board.cells[x][y].cellType.isBomb()) assertTrue(board.isFullyOpen())
            else assertFalse(board.isFullyOpen())
        }
    }

    @Test
    fun openedAllExceptBombs() {
        openedAllExceptBombsTest(9, 9, 10)
        openedAllExceptBombsTest(10, 10, 91)
        openedAllExceptBombsTest(25, 45, 90)
    }

    private fun openedAllExceptBombsTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        assertFalse(board.openedAllExceptBombs())

        for (i in 0 until width) {
            for (j in 0 until height) {
                if (!board.cells[i][j].cellType.isBomb() && board.cells[i][j].cellState != CellState.OPENED) {
                    board.openCells(i, j)
                }
            }
        }

        assertTrue(board.openedAllExceptBombs())
    }

    @Test
    fun handleFlag() {
        handleFlagTest(9, 9, 10)
        handleFlagTest(10, 10, 91)
        handleFlagTest(25, 45, 90)
    }

    private fun handleFlagTest(width: Int, height: Int, bombs: Int) {
        initBoard(width, height, bombs)

        board.cells.forEach { it.forEach { cell -> assertNotEquals(CellState.FLAGGED, cell.cellState) } }

        for (i in 0 until width) {
            for (j in 0 until height) {
               board.handleFlag(i, j)
            }
        }

        board.cells.forEach { it.forEach { cell -> assertEquals(CellState.FLAGGED, cell.cellState) } }

        for (i in 0 until width) {
            for (j in 0 until height) {
                board.handleFlag(i, j)
            }
        }

        board.cells.forEach { it.forEach { cell -> assertNotEquals(CellState.FLAGGED, cell.cellState) } }
    }

    companion object {
        private const val ITERATIONS: Int = 25
    }
}