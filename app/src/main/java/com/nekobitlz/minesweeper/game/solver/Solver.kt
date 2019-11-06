package com.nekobitlz.minesweeper.game.solver

import android.util.Log
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.enums.GameState.*
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.collections.HashSet
import kotlin.random.Random

class Solver {

    private var changed = true
    private var cellGroups = mutableSetOf<CellGroup>()
    val gameEngine = CacheManager.loadEngine()

    fun solve() {
        var logCount = 0

        while (changed && !gameEngine.isFinished()) {
            logCount++
            Log.d("SOLVER", "Solve: while #$logCount")

            changed = false
            cellGroups = getCellGroups()

            if (gameEngine.gameState == NO_STATE) randomMove()

            subtractionMethod()

            if (!changed && cellGroups.size > 0 && !gameEngine.isFinished()) probabilityMethod()
            if (!changed && cellGroups.size == 0 && !gameEngine.isFinished()) randomMove()
        }
    }

    private fun getCellGroups(): MutableSet<CellGroup> {
        Log.d("Solver", "getCellGroups: init")
        val cellGroups = mutableSetOf<CellGroup>()
        val openedCells = gameEngine.getCells().map { it.filter { cell -> cell.cellState.isOpened() && cell.cellType == CellType.COVERED } }
        Log.d("Solver", "getCellGroups: openedCells")

        openedCells.forEach {
            it.forEach { cell ->
                val currentCellGroup = CellGroup(cell)

                if (currentCellGroup.isNotEmpty()) {
                    cellGroups.forEach cellGroups@{ cellGroup ->
                        Log.d("Solver", "getCellGroups: forEach")
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
        Log.d("Solver", "randomMove: init")
        val isolatedCells = getIsolatedCells()
        val isolatedCount = isolatedCells.size

        if (cellGroups.isEmpty() && isolatedCount == gameEngine.board.remainingFlags) {
            isolatedCells.forEach { it.forEach { cell -> gameEngine.handleLongPress(cell.x, cell.y) } }
            Log.d("Solver", "randomMove: handleFlag")
        } else {
            val cellCord = getRandomIsolatedCell(isolatedCount)
            gameEngine.handleShortPress(cellCord.first, cellCord.second)
            Log.d("Solver", "randomMove: openCells")
        }

        changed = isolatedCount > 0
        cellGroups = getCellGroups()
    }

    private fun getIsolatedCells(): MutableList<MutableList<Cell>> {
        Log.d("Solver", "getIsolatedCells: init")
        val isolatedCells = gameEngine.getCells().map { it.toMutableList() }.toMutableList() //TODO make linear array
        Log.d("Solver", "getIsolatedCells: isolatedCells")

        gameEngine.getCells().map {
            it.forEach { cell ->
                val neighbours = gameEngine.board.getNeighbours(cell)
                isolatedCells.map { isolated -> isolated.removeAll(neighbours) }
                Log.d("Solver", "getIsolatedCells: neighbours")
            }
        }

        return isolatedCells
    }

    private fun getRandomIsolatedCell(isolatedCount: Int): Pair<Int, Int> {
        Log.d("Solver", "getRandomIsolatedCell: init")
        val random = Random(seed = isolatedCount)
        var indexX = random.nextInt(isolatedCount)
        var indexY = random.nextInt(isolatedCount)

        while (indexX == indexY) {
            indexX = random.nextInt(isolatedCount)
            indexY = random.nextInt(isolatedCount)
        }

        Log.d("Solver", "getRandomIsolatedCells: $indexX $indexY")

        return indexX to indexY
    }

    private fun subtractionMethod() {
        Log.d("Solver", "subtractionMethod: init")

        cellGroups.forEach {
            when {
                it.bombsCount == 0 -> {
                    it.forEach { cell -> gameEngine.handleShortPress(cell.x, cell.y) }
                    changed = true
                    Log.d("Solver", "subtractionMethod: openedAll")
                }
                it.bombsCount == it.size -> {
                    it.forEach { cell -> if (!cell.cellState.isFlagged()) gameEngine.handleLongPress(cell.x, cell.y) }
                    changed = true
                    Log.d("Solver", "subtractionMethod: handleFlag")
                }
            }
        }

        cellGroups = getCellGroups()
    }

    private fun probabilityMethod() {
        Log.d("Solver", "probabilityMethod: init")
        val probabilities = hashMapOf<Cell, Double>()

        cellGroups.forEach { cellGroup ->
            if (cellGroup.size > 0) {
                cellGroup.forEach { cell ->
                    val indieProbability = cellGroup.bombsCount / cellGroup.size.toDouble()
                    val oldProbability = if (probabilities[cell] != null) probabilities[cell]!! else 0.0
                    val newProbability = 1 - (1 - indieProbability) * (1 - oldProbability)

                    probabilities.put(cell, newProbability)
                    Log.d("Solver", "probabilityMethod: probability initiation")
                }
            }
        }

        val maxProbabilityEntry = probabilities.maxBy { it.value }!!
        val maxProbability = maxProbabilityEntry.value
        val maxProbabilityCell = maxProbabilityEntry.key
        Log.d("Solver", "probabilityMethod: maxProb")

        val minProbabilityEntry = probabilities.minBy { it.value }!!
        val minProbability = minProbabilityEntry.value
        val minProbabilityCell = minProbabilityEntry.key
        Log.d("Solver", "probabilityMethod: minProb")

        val isolatedCount = getIsolatedCells().size

        if (isolatedCount > 0) {
            var bombCellGroupCount = 0

            cellGroups.forEach { bombCellGroupCount += it.bombsCount } //cellGroups.map { it.bombsCount }.count()
            Log.d("Solver", "probabilityMethod: bombCellGroup")

            val randomOpenProbability = (gameEngine.board.remainingFlags - bombCellGroupCount) / isolatedCount.toDouble()

            if (randomOpenProbability < minProbability && randomOpenProbability < 1 - maxProbability) {
                randomMove()
                Log.d("Solver", "probabilityMethod: randomMove")
            }
        }

        if (!changed) {
            if (minProbability < 1 - maxProbability) {
                if (minProbabilityCell.x !in 0 until 9 || minProbabilityCell.y !in 0 until 9) {
                    Log.d("Solver", "probabilityMethod: CATCH EXCEPTION ${minProbabilityCell.x} ${minProbabilityCell.y} $minProbability")
                } else {
                    gameEngine.handleShortPress(minProbabilityCell.x, minProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: openMinProb")
                }
            } else {
                if (maxProbabilityCell.x !in 0 until 9 || maxProbabilityCell.y !in 0 until 9) {
                    Log.d("Solver", "probabilityMethod: CATCH EXCEPTION ${maxProbabilityCell.x} ${maxProbabilityCell.y} $maxProbability")
                } else {
                    gameEngine.handleShortPress(maxProbabilityCell.x, maxProbabilityCell.y)
                    Log.d("Solver", "probabilityMethod: openMaxProb")

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
                Log.d("Solver", "CellGroup: init")
            }
        }

        fun includes(cellGroup: CellGroup): Boolean {
            Log.d("Solver", "CellGroup: includes")
            return containsAll(cellGroup) && bombsCount >= cellGroup.bombsCount
        }

        fun subtract(cellGroup: CellGroup) = apply {
            removeAll(cellGroup)
            bombsCount -= cellGroup.bombsCount
            Log.d("Solver", "CellGroup: subtract")
        }
    }
}