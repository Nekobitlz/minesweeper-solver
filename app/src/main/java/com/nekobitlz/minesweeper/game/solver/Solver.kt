package com.nekobitlz.minesweeper.game.solver

import android.util.Log
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.enums.GameState.NO_STATE
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.collections.Map.*
import kotlin.random.Random

class Solver {

    private val gameEngine = CacheManager.loadEngine()
    private val width = CacheManager.COLUMN_COUNT
    private val height = CacheManager.ROW_COUNT

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
        val openedCells = gameEngine.getCells().map {
            it.filter { cell -> cell.cellState.isOpened() && cell.cellType == CellType.COVERED }
        }

        for (cellX in openedCells) {
            for (cellY in cellX) {
                val currentCellGroup = CellGroup(cellY)

                if (currentCellGroup.isNotEmpty()) {
                    cellGroups.forEach cellGroups@{
                        when {
                            currentCellGroup == it -> return@cellGroups
                            currentCellGroup.includes(it) -> currentCellGroup.subtract(it)
                            it.includes(currentCellGroup) -> it.subtract(currentCellGroup)
                        }
                    }

                    cellGroups.add(currentCellGroup)
                }
            }
        }

        return cellGroups
    }

    private fun randomMove() {
        val isolatedCells = gameEngine.board.isolatedClosedCells
        val isolatedCount = isolatedCells.size

        if (cellGroups.isEmpty() && isolatedCount == gameEngine.board.remainingFlags) {
            isolatedCells.forEach { cell -> gameEngine.handleLongPress(cell.x, cell.y) }
            Log.d("Solver", "randomMove: handleLongPress")
        }
        else when (gameEngine.gameState) {
            NO_STATE -> {
                val x = Random(width).nextInt(width - 1)
                val y = Random(height).nextInt(height - 1)

                gameEngine.handleShortPress(x, y)
            }
            else -> {
                val cellCord = isolatedCells[getRandomIsolatedCell(isolatedCount)]
                gameEngine.handleShortPress(cellCord.x, cellCord.y)
                Log.d("Solver", "randomMove: handleShortPress ${cellCord.x} ${cellCord.y}")
            }
        }

        changed = isolatedCount > 0
        cellGroups = getCellGroups()
    }

    private fun getRandomIsolatedCell(isolatedCount: Int): Int {
        val random = Random(seed = isolatedCount)

        return random.nextInt(isolatedCount)
    }

    private fun subtractionMethod() {
        for (cellGroup in cellGroups) {
            when {
                cellGroup.bombsCount == 0 -> {
                    cellGroup.forEach { cell -> gameEngine.handleShortPress(cell.x, cell.y) }
                    Log.d("Solver", "subtractionMethod: handleShortPress")
                    changed = true
                }
                cellGroup.bombsCount == cellGroup.size -> {
                    cellGroup.forEach { cell ->
                        if (!cell.cellState.isFlagged()) gameEngine.handleLongPress(cell.x, cell.y)
                    }
                    Log.d("Solver", "subtractionMethod: handleLongPress")
                    changed = true
                }
            }
        }

        cellGroups = getCellGroups()
    }

    private fun probabilityMethod() {
        val probabilities = initProbabilities()

        val maxProbabilityEntry = probabilities.maxBy { it.value }!!
        val minProbabilityEntry = probabilities.minBy { it.value }!!

        openByRandom(minProbabilityEntry.value, maxProbabilityEntry.value)
        openByProbability(minProbabilityEntry, maxProbabilityEntry)
    }

    private fun initProbabilities(): HashMap<Cell, Double> {
        val probabilities = hashMapOf<Cell, Double>()

        for (cellGroup in cellGroups) {
            if (cellGroup.isNotEmpty()) {
                for (cell in cellGroup) {
                    val indieProbability = cellGroup.bombsCount / cellGroup.size.toDouble()
                    val oldProbability = if (probabilities[cell] != null) probabilities[cell]!! else 0.0
                    val newProbability = 1 - (1 - indieProbability) * (1 - oldProbability)

                    probabilities[cell] = newProbability
                }
            }
        }

        return probabilities
    }

    private fun openByRandom(minProbability: Double, maxProbability: Double) {
        val isolatedCount = gameEngine.board.isolatedClosedCells.size

        if (isolatedCount > 0) {
            var bombCellGroupCount = 0

            cellGroups.forEach { bombCellGroupCount += it.bombsCount }

            val randomOpenProbability = (gameEngine.board.remainingFlags - bombCellGroupCount) / isolatedCount.toDouble()

            if (randomOpenProbability < minProbability && randomOpenProbability < 1 - maxProbability) {
                randomMove()
                Log.d("Solver", "probabilityMethod: randomMove")
            }
        }
    }

    private fun openByProbability(minProbabilityEntry: Entry<Cell, Double>,
                                  maxProbabilityEntry: Entry<Cell, Double>) {
        val minProbability = minProbabilityEntry.value
        val minProbabilityCell = minProbabilityEntry.key

        val maxProbability = maxProbabilityEntry.value
        val maxProbabilityCell = maxProbabilityEntry.key

        if (!changed) {
            when {
                minProbability < 1 - maxProbability && minProbabilityCell.x in 0 until width && minProbabilityCell.y in 0 until height -> {
                    gameEngine.handleShortPress(minProbabilityCell.x, minProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: handleShortPress")
                }
                maxProbabilityCell.x in 0 until width && maxProbabilityCell.y in 0 until height -> {
                    gameEngine.handleLongPress(maxProbabilityCell.x, maxProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: handleLongPress")
                }
            }

            changed = true
            cellGroups = getCellGroups()
        }
    }
}