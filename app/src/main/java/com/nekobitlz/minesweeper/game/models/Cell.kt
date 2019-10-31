package com.nekobitlz.minesweeper.game.models

import androidx.annotation.VisibleForTesting
import com.nekobitlz.minesweeper.game.enums.CellState.*
import com.nekobitlz.minesweeper.game.enums.CellType

class Cell(val x: Int, val y: Int) {
    var cellType = CellType.COVERED
    var bombsNearby = 0

    var cellState = NO_STATE
    var previousState = NO_STATE

    fun open() {
        cellState = OPENED
    }

    fun putFlag() {
        previousState = cellState
        cellState = FLAGGED
    }

    fun removeFlag() {
        if (cellState == FLAGGED) cellState = previousState
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override fun toString(): String {
        return "Cell(x=$x, y=$y, cellType=$cellType, bombsNearby=$bombsNearby)"
    }
}