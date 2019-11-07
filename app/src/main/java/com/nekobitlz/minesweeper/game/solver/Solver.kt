package com.nekobitlz.minesweeper.game.solver

import android.util.Log
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.enums.GameState.*
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.collections.HashSet
import kotlin.random.Random

class Solver {

    private val gameEngine = CacheManager.loadEngine()
    private var changed = true
    private var cellGroups = mutableSetOf<CellGroup>()

    fun solve() {
        while (changed && !gameEngine.isFinished()) {
            changed = false
            cellGroups = getCellGroups()

            if (gameEngine.gameState == NO_STATE) randomMove()

            subtractionMethod()

            if (!changed && cellGroups.size > 0 && !gameEngine.isFinished()) probabilityMethod()
            if (!changed && cellGroups.size == 0 && !gameEngine.isFinished()) randomMove()
        }
    }

    private fun getCellGroups(): MutableSet<CellGroup> {
        val cellGroups = mutableSetOf<CellGroup>()
        val openedCells = gameEngine.getCells().map { it.filter { cell -> cell.cellState.isOpened() && cell.cellType == CellType.COVERED } }

        openedCells.forEach {
            it.forEach { cell ->
                val currentCellGroup = CellGroup(cell)

                if (currentCellGroup.isNotEmpty()) {
                    cellGroups.forEach cellGroups@{ cellGroup ->
                        when {
                            currentCellGroup == cellGroup -> return@cellGroups
                            currentCellGroup.includes(cellGroup) -> currentCellGroup.subtract(cellGroup)
                            cellGroup.includes(currentCellGroup) -> cellGroup.subtract(currentCellGroup)
                        }
                    }

                    cellGroups.add(currentCellGroup)
                }
            }
        }

        return cellGroups
    }

    private fun randomMove() {
        val isolatedCells = getIsolatedCells()
        val isolatedCount = isolatedCells.size

        if (cellGroups.isEmpty() && isolatedCount == gameEngine.board.remainingFlags) {
            isolatedCells.forEach { cell -> gameEngine.handleLongPress(cell.x, cell.y) }
            Log.d("Solver", "randomMove: handleLongPress")
        } else {
            val cellCord = isolatedCells[getRandomIsolatedCell(isolatedCount)]
            gameEngine.handleShortPress(cellCord.x, cellCord.y)
            Log.d("Solver", "randomMove: handleShortPress ${cellCord.x} ${cellCord.y}")
        }

        changed = isolatedCount > 0
        cellGroups = getCellGroups()
    }

    private fun getIsolatedCells(): MutableList<Cell> {
        val isolatedCells = mutableListOf<Cell>()
        gameEngine.getCells().forEach { it.forEach { cell -> isolatedCells.add(cell) } }
        val copy = isolatedCells.toList()

        copy.map { cell ->
            val neighbours = gameEngine.board.getNeighbours(cell)
            isolatedCells.removeAll(neighbours)
        }

        return isolatedCells
    }

    private fun getRandomIsolatedCell(isolatedCount: Int): Int {
        val random = Random(seed = isolatedCount)

        return random.nextInt(isolatedCount + 1)
    }

    private fun subtractionMethod() {
        cellGroups.forEach {
            when {
                it.bombsCount == 0 -> {
                    it.forEach { cell -> gameEngine.handleShortPress(cell.x, cell.y) }
                    Log.d("Solver", "subtractionMethod: handleShortPress")
                    changed = true
                }
                it.bombsCount == it.size -> {
                    it.forEach { cell -> if (!cell.cellState.isFlagged()) gameEngine.handleLongPress(cell.x, cell.y) }
                    Log.d("Solver", "subtractionMethod: handleLongPress")
                    changed = true
                }
            }
        }

        cellGroups = getCellGroups()
    }

    private fun probabilityMethod() {
        val probabilities = hashMapOf<Cell, Double>()

        cellGroups.forEach { cellGroup ->
            if (cellGroup.size > 0) {
                cellGroup.forEach { cell ->
                    val indieProbability = cellGroup.bombsCount / cellGroup.size.toDouble()
                    val oldProbability = if (probabilities[cell] != null) probabilities[cell]!! else 0.0
                    val newProbability = 1 - (1 - indieProbability) * (1 - oldProbability)

                    probabilities[cell] = newProbability
                }
            }
        }

        val maxProbabilityEntry = probabilities.maxBy { it.value }!!
        val maxProbability = maxProbabilityEntry.value
        val maxProbabilityCell = maxProbabilityEntry.key

        val minProbabilityEntry = probabilities.minBy { it.value }!!
        val minProbability = minProbabilityEntry.value
        val minProbabilityCell = minProbabilityEntry.key

        val isolatedCount = getIsolatedCells().size

        if (isolatedCount > 0) {
            var bombCellGroupCount = 0

            cellGroups.forEach { bombCellGroupCount += it.bombsCount } //cellGroups.map { it.bombsCount }.count()

            val randomOpenProbability = (gameEngine.board.remainingFlags - bombCellGroupCount) / isolatedCount.toDouble()

            if (randomOpenProbability < minProbability && randomOpenProbability < 1 - maxProbability) {
                randomMove()
            }
        }

        if (!changed) {
            if (minProbability < 1 - maxProbability) {
                if (minProbabilityCell.x in 0 until 9 && minProbabilityCell.y in 0 until 9) {
                    gameEngine.handleShortPress(minProbabilityCell.x, minProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: handleShortPress")
                }
            } else {
                if (maxProbabilityCell.x in 0 until 9 && maxProbabilityCell.y in 0 until 9) {
                    gameEngine.handleLongPress(maxProbabilityCell.x, maxProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: handleLongPress")
                }
            }

            changed = true
            cellGroups = getCellGroups()
        }
    }

    private inner class CellGroup(cell: Cell): HashSet<Cell>() {

        internal var bombsCount = 0

        init {
            if (cell.cellState.isOpened()) {
                val neighbours = gameEngine.board.getNeighbours(cell)
                val openedNeighbours = neighbours.filter { !it.cellState.isOpened() && !it.cellState.isFlagged() }
                val flaggedNeighbours = neighbours.filter { it.cellState.isFlagged() }.count()

                addAll(openedNeighbours)
                bombsCount = cell.bombsNearby - flaggedNeighbours
            }
        }

        fun includes(cellGroup: CellGroup): Boolean {
            return containsAll(cellGroup) && bombsCount >= cellGroup.bombsCount
        }

        fun subtract(cellGroup: CellGroup) = apply {
            removeAll(cellGroup)
            bombsCount -= cellGroup.bombsCount
        }
    }
}