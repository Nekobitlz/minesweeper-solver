package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.random.Random

class Board(private val width: Int, private val height: Int, private val bombsCount: Int) {

    val cells = Array(width) { Array(height) { Cell(0, 0) } }
    private var isFullyOpen = false

    fun initGame(x: Int, y: Int) {
        generateCells()
        createBombs(x, y)
        setNeighborBombsCount()
        findEmptyCells()
        isFullyOpen = false
    }

    fun openCells(x: Int, y: Int) {

        val cell = cells[x][y]

        cell.open()

        when (cell.cellType) {
            CellType.BOMB -> openAllCells()
            CellType.EMPTY -> {
                for (nearestX in (x - 1)..(x + 1)) {
                    for (nearestY in (y - 1)..(y + 1)) {
                        if (coordinatesAreValid(nearestX, nearestY)
                            && cells[nearestX][nearestY] != cells[x][y]
                            && !cells[nearestX][nearestY].isOpened
                        ) {
                            openCells(nearestX, nearestY)
                        }
                    }
                }
            }
            CellType.COVERED -> { /* nothing */ }
        }
    }

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

        if (cells[x][y].cellType == CellType.BOMB) {
            createBombAtRandomPoint(clickX, clickY)
        } else {
            cells[x][y].cellType = CellType.BOMB
            return
        }
    }

    private fun setNeighborBombsCount() {

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (cells[x][y].cellType == CellType.BOMB) {
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

                if (currentCell.bombsNearby == 0 && currentCell.cellType != CellType.BOMB) {
                    currentCell.cellType = CellType.EMPTY
                }
            }
        }
    }

    fun isFullyOpen(): Boolean = isFullyOpen
}
