package com.nekobitlz.minesweeper.game.solver

import com.nekobitlz.minesweeper.game.engine.Board
import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.enums.GameState.*
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SolverTest {

    private val board1 = TestBoard(9, 9, 10)
    private val board2 = TestBoard(16, 16, 40)
    private val board3 = TestBoard(24, 24, 99)

    @Test
    fun solve() {
        val gameEngine = TestEngine()

        // 9 - 9 - 10 - Beginner test
        gameEngine.setBoard(board1)

        var yList = mutableListOf(5, 7, 8, 5, 6, 2, 4, 5, 4, 3)
        var xList = mutableListOf(1, 2, 2, 4, 4, 5, 5, 6, 7, 8)

        solve(gameEngine, xList, yList)
        assertEquals(WIN, gameEngine.gameState)

        // 16 - 16 - 40 - Intermediate
        gameEngine.reset()
        gameEngine.setBoard(board2)

        xList = mutableListOf(0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 7, 8,
            8, 8, 8, 9, 9, 11, 11, 11, 12, 12, 12, 13, 13, 14, 14, 14, 15)
        yList = mutableListOf(9, 3, 6, 8, 13, 3, 11, 3, 6, 13, 2, 10, 12, 13, 4, 10, 12, 14, 0, 10,
            12, 14, 0, 4, 8, 12, 0, 5, 0, 5, 6, 4, 8, 15, 7, 8, 0, 3, 10, 1)

        solve(gameEngine, xList, yList)
        assertEquals(WIN, gameEngine.gameState)

        // 24 - 24 - 99 - Intermediate
        gameEngine.reset()
        gameEngine.setBoard(board3)

        xList = mutableListOf(0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5,
            6, 6, 6, 6, 6, 7, 7, 7, 7, 8, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 12,
            12, 12, 12, 12, 12, 13, 14, 14, 14, 14, 15, 15, 15, 15, 15, 15, 15, 15, 16, 16, 16, 16,
            17, 17, 17, 18, 18, 18, 18, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20, 20, 20, 20, 21,
            21, 21, 21, 22, 22, 23, 23)
        yList = mutableListOf(1, 22, 4, 8, 9, 10, 20, 6, 12, 15, 16, 21, 5, 6, 12, 4, 6, 9, 15, 16,
            21, 14, 20, 11, 12, 14, 15, 18, 12, 13, 15, 20, 16, 1, 12, 14, 20, 4, 14, 17, 18, 19,
            21, 2, 14, 16, 22, 1, 4, 6, 8, 11, 21, 21, 0, 5, 7, 22, 1, 4, 6, 7, 9, 11, 12, 20, 3,
            12, 14, 17, 2, 5, 15, 5, 8, 18, 21, 1, 4, 5, 7, 9, 16, 17, 20, 2, 6, 9, 15, 17, 18, 0,
            3, 6, 21, 15, 16, 8, 17)

        solve(gameEngine, xList, yList)
        assertEquals(WIN, gameEngine.gameState)

        // 24 - 24 - 99 - Intermediate - Unreal
        gameEngine.reset()
        gameEngine.setBoard(board3)

        xList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4,
            5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 10,
            10, 11, 12, 12, 12, 12, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 16, 17, 17, 17, 17,
            17, 18, 18, 19, 19, 19, 19, 20, 20, 20, 20, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22,
            22, 23, 23, 23)
        yList = mutableListOf(1, 5, 9, 12, 17, 19, 21, 0, 1, 3, 7, 17, 21, 6, 12, 13, 1, 5, 9, 10,
            16, 19, 22, 3, 5, 6, 7, 17, 18, 1, 4, 12, 16, 18, 0, 12, 18, 21, 1, 12, 15, 16, 19, 22,
            8, 11, 20, 22, 23, 2, 5, 10, 16, 5, 7, 10, 21, 22, 23, 7, 9, 12, 8, 9, 18, 2, 8, 13, 22,
            1, 6, 12, 17, 23, 2, 13, 4, 8, 18, 22, 3, 10, 11, 16, 3, 6, 7, 8, 9, 12, 21, 4, 7, 8,
            12, 22, 10, 13, 20)

        solve(gameEngine, xList, yList)
        assertEquals(GAME_OVER, gameEngine.gameState)
    }

    private fun solve(engine: TestEngine, xList: List<Int>, yList: List<Int>) {
        val solver = Solver()
        engine.xList = xList
        engine.yList = yList
        solver.initSolver(engine, engine.board.cells[0].size, engine.board.cells.size)
        solver.solve()
    }

    private class TestBoard(width: Int, height: Int, val bombsCount: Int): Board(width, height, bombsCount) {

        fun initBeforeSetting() {
            generateCells()
        }

        fun setBomb(xList: List<Int>, yList: List<Int>) {
            for (i in 0 until bombsCount)
                    cells[xList[i]][yList[i]].cellType = CellType.BOMB
        }

        fun initAfterSetting() {
            setNeighborBombsCount()
            findEmptyCells()

            remainingFlags = bombsCount
        }
    }

    private class TestEngine : GameEngine() {

        var xList = listOf<Int>()
        var yList = listOf<Int>()

        fun setBoard(board: TestBoard) {
            this.board = board
        }

        override fun start(x: Int, y: Int) {
            gameState = PLAYING

            with(board) {
                val thisBoard = board as TestBoard
                thisBoard.initBeforeSetting()
                thisBoard.setBomb(xList, yList)
                thisBoard.initAfterSetting()
                openCells(x, y)
            }
        }
    }
}