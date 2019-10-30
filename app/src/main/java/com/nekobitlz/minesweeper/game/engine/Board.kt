package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.CellType.*
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.random.Random

class Board(private val width: Int, private val height: Int, private val bombsCount: Int) {

    val cells = Array(width) { Array(height) { Cell(0, 0) } }

    private val size = width * height
    private var isFullyOpen = false
    private var cellsCount = 0

    fun initGame(x: Int, y: Int) {
        generateCells()
        createBombs(x, y)
        setNeighborBombsCount()
        findEmptyCells()

        isFullyOpen = false
        cellsCount = 0
    }

    fun openCells(x: Int, y: Int) {
        val cell = cells[x][y]

        cell.open()

        when (cell.cellType) {
            BOMB -> openAllCells()
            EMPTY -> {
                cellsCount++

                for (nearestX in (x - 1)..(x + 1)) {
                    for (nearestY in (y - 1)..(y + 1)) {
                        if (coordinatesAreValid(nearestX, nearestY) &&
                            cells[nearestX][nearestY] != cells[x][y] &&
                            !cells[nearestX][nearestY].isOpened
                        ) {
                            openCells(nearestX, nearestY)
                        }
                    }
                }
            }
            COVERED -> { cellsCount++ }
        }
    }

    fun isFullyOpen(): Boolean = isFullyOpen

    fun closedAllExceptBombs(): Boolean = cellsCount == size - bombsCount

    private fun openAllCells() {
        cells.forEach { it.forEach { cell -> cell.open() } }
        isFullyOpen = true
    }

    private fun generateCells() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                cells[x][y] = Cell(x, y)
            }
        }
    }

    private fun createBombs(clickX: Int, clickY: Int) {
        for (bomb in 0 until bombsCount) {
            createBombAtRandomPoint(clickX, clickY)
        }
    }

    private fun createBombAtRandomPoint(clickX: Int, clickY: Int) {
        val x = Random.nextInt(width)
        val y = Random.nextInt(height)

        if (x == clickX && y == clickY) {
            createBombAtRandomPoint(clickX, clickY)
            return
        }

        if (cells[x][y].cellType.isBomb()) {
            createBombAtRandomPoint(clickX, clickY)
        } else {
            cells[x][y].cellType = BOMB
            return
        }
    }

    private fun setNeighborBombsCount() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (cells[x][y].cellType.isBomb()) {
                    setBombsCountForNearestCells(x, y)
                }
            }
        }
    }

    private fun setBombsCountForNearestCells(x: Int, y: Int) {
        for (nearestX in (x - 1)..(x + 1)) {
            for (nearestY in (y - 1)..(y + 1)) {
                if (coordinatesAreValid(nearestX, nearestY) && cells[nearestX][nearestY] != cells[x][y]) {
                    cells[nearestX][nearestY].bombsNearby++
                }
            }
        }
    }

    private fun coordinatesAreValid(x: Int, y: Int): Boolean = x != -1 && y != -1 && x != width && y != height

    private fun findEmptyCells() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                val currentCell = cells[x][y]

                if (currentCell.bombsNearby == 0 && currentCell.cellType != BOMB) {
                    currentCell.cellType = EMPTY
                }
            }
        }
    }
}