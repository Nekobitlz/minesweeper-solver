package com.nekobitlz.minesweeper.game.solver

import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell

internal class CellGroup(cell: Cell): HashSet<Cell>() {

    internal var bombsCount = 0
    private val gameEngine = CacheManager.loadEngine()

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