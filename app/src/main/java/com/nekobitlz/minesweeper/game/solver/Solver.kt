package com.nekobitlz.minesweeper.game.solver

import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.enums.GameState.NO_STATE
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell
import kotlin.collections.Map.*
import kotlin.random.Random

class Solver {

    private var gameEngine = CacheManager.loadEngine()
    private var width = CacheManager.COLUMN_COUNT
    private var height = CacheManager.ROW_COUNT

    private var changed = true
    private var cellGroups = setOf<CellGroup>()

    fun initSolver(gameEngine: GameEngine, width: Int, height: Int) {
        this.gameEngine = gameEngine
        this.width = width
        this.height = height
    }

    fun solve() {
        while (changed && !gameEngine.isFinished()) {
            changed = false
            cellGroups = getCellGroups()

            if (gameEngine.gameState == NO_STATE) randomMove()

            subtractionMethod()

            if (!changed && cellGroups.isNotEmpty() && !gameEngine.isFinished()) probabilityMethod()
            if (!changed && cellGroups.isEmpty() && !gameEngine.isFinished()) randomMove()
        }
    }

    private fun getCellGroups(): Set<CellGroup> {
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
        } else {
            when (gameEngine.gameState) {
                NO_STATE -> {
                    val x = Random(width).nextInt(width - 1)
                    val y = Random(height).nextInt(height - 1)

                    gameEngine.handleShortPress(x, y)
                }
                else -> {
                    val cellCord = isolatedCells[getRandomIsolatedCell(isolatedCount)]
                    gameEngine.handleShortPress(cellCord.x, cellCord.y)
                }
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
                    changed = true
                }
                cellGroup.bombsCount == cellGroup.size -> {
                    cellGroup.forEach { cell ->
                        if (!cell.cellState.isFlagged()) gameEngine.handleLongPress(cell.x, cell.y)
                    }
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

    private fun initProbabilities(): Map<Cell, Double> {
        val probabilities = mutableMapOf<Cell, Double>()

        for (cellGroup in cellGroups) {
            if (cellGroup.isNotEmpty()) {
                for (cell in cellGroup) {
                    val indieProbability = cellGroup.bombsCount / cellGroup.size.toDouble()
                    val oldProbability = probabilities.getOrElse(cell, { 0.0 }) //getOrDefault requires API level 24 (current min is 23)
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
                }
                maxProbabilityCell.x in 0 until width && maxProbabilityCell.y in 0 until height -> {
                    gameEngine.handleLongPress(maxProbabilityCell.x, maxProbabilityCell.y)
                }
            }

            changed = true
            cellGroups = getCellGroups()
        }
    }

    inner class CellGroup(cell: Cell): HashSet<Cell>() {

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