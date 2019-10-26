package com.nekobitlz.minesweeper.game.engine

import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.random.Random

class Board(private val width: Int, private val height: Int, private val bombsCount: Int) {

    val cells = Array(width) { Array(height) { Cell(0, 0) } }

    init {
        generateCells()
        createBombs()
        setNeighborBombsCount()
        findEmptyCells()
    }

    private fun generateCells() {

        for (x in 0 until width) {
            for (y in 0 until height) {
                cells[x][y] = Cell(x, y)
            }
        }
    }

    private fun createBombs() {

        for (bomb in 0 until bombsCount) {
            createBombAtRandomPoint()
        }
    }

    private fun createBombAtRandomPoint() {

        val x = Random.nextInt(width)
        val y = Random.nextInt(height)

        if (cells[x][y].cellType == CellType.BOMB) {
            createBombAtRandomPoint()
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
                    cells[nearestX][nearestY].cellType = CellType.COVERED
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
}
