package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.CellState.NO_STATE
import com.nekobitlz.minesweeper.game.enums.CellType.*
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.random.Random

internal class Board(private val width: Int, private val height: Int, private val bombsCount: Int) {

    internal val cells = Array(width) { Array(height) { Cell(0, 0) } }

    private val size = width * height
    private var isFullyOpen = false
    private var cellsCount = 0

    internal fun initGame(x: Int, y: Int) {
        generateCells()
        createBombs(x, y)
        setNeighborBombsCount()
        findEmptyCells()

        isFullyOpen = false
        cellsCount = 0
    }

    internal fun openCells(x: Int, y: Int) {
        val cell = cells[x][y]

        if (!cell.cellState.isFlagged()) {
            cell.open()

            when (cell.cellType) {
                BOMB -> openAllCells()
                EMPTY -> {
                    cellsCount++
                    openNearestCells(x, y)
                }
                COVERED -> {
                    cellsCount++
                }
            }
        }
    }

    internal fun isFullyOpen(): Boolean = isFullyOpen

    internal fun openedAllExceptBombs(): Boolean = cellsCount == size - bombsCount

    internal fun handleFlag(x: Int, y: Int) {
        val cell = cells[x][y]

        if (cell.cellState.isFlagged()) cell.removeFlag()
        else cell.putFlag()
    }

    internal fun reset() = forEachCell { x, y ->
        val currentCell = cells[x][y]

        currentCell.cellState = NO_STATE
        currentCell.cellType = COVERED
        currentCell.bombsNearby = 0
        isFullyOpen = false
        cellsCount = 0
    }

    private fun openAllCells() {
        forEachCell { x, y -> cells[x][y].open() }
        isFullyOpen = true
    }

    private fun openNearestCells(x: Int, y: Int) = forEachNearestCell(x, y) { nearestX, nearestY ->
        if (coordinatesAreValid(nearestX, nearestY) &&
            cells[nearestX][nearestY] != cells[x][y] &&
            cells[nearestX][nearestY].cellState == NO_STATE
        ) {
            openCells(nearestX, nearestY)
        }
    }

    private fun generateCells() = forEachCell { x, y -> cells[x][y] = Cell(x, y) }

    private fun createBombs(clickX: Int, clickY: Int) {
        for (bomb in 0 until bombsCount) {
            createBombAtRandomPoint(clickX, clickY)
        }
    }

    private fun createBombAtRandomPoint(targetX: Int, targetY: Int) {
        val x = Random.nextInt(width)
        val y = Random.nextInt(height)

        if (x == targetX && y == targetY) {
            createBombAtRandomPoint(targetX, targetY)
            return
        }

        if (cells[x][y].cellType.isBomb()) {
            createBombAtRandomPoint(targetX, targetY)
        } else {
            cells[x][y].cellType = BOMB
            return
        }
    }

    private fun setNeighborBombsCount() = forEachCell { x, y ->
        if (cells[x][y].cellType.isBomb()) {
            setBombsCountForNearestCells(x, y)
        }
    }

    private fun setBombsCountForNearestCells(x: Int, y: Int) {
        forEachNearestCell(x, y) { nearestX, nearestY ->
            if (coordinatesAreValid(nearestX, nearestY) &&
                cells[nearestX][nearestY] != cells[x][y]
            ) {
                cells[nearestX][nearestY].bombsNearby++
            }
        }
    }

    private fun coordinatesAreValid(x: Int, y: Int): Boolean = x != -1 && y != -1 && x != width && y != height

    private fun findEmptyCells() = forEachCell { x, y ->
        val currentCell = cells[x][y]

        if (currentCell.bombsNearby == 0 && currentCell.cellType != BOMB) {
            currentCell.cellType = EMPTY
        }
    }

    private fun forEachCell(func: (x: Int, y: Int) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                func(x, y)
            }
        }
    }

    private fun forEachNearestCell(x: Int, y: Int, func: (nearestX: Int, nearestY: Int) -> Unit) {
        for (nearestX in (x - 1)..(x + 1)) {
            for (nearestY in (y - 1)..(y + 1)) {
                func(nearestX, nearestY)
            }
        }
    }
}