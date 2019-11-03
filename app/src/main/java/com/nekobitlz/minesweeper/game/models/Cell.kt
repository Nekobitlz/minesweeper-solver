package com.nekobitlz.minesweeper.game.models

import com.nekobitlz.minesweeper.game.enums.CellState.*
import com.nekobitlz.minesweeper.game.enums.CellType

class Cell(val x: Int, val y: Int) {
    internal var cellType = CellType.COVERED
    internal var bombsNearby = 0

    internal var cellState = NO_STATE
    private var previousState = NO_STATE

    internal fun open() {
        cellState = OPENED
    }

    internal fun putFlag() {
        previousState = cellState
        cellState = FLAGGED
    }

    internal fun removeFlag() {
        if (cellState == FLAGGED) cellState = previousState
    }
}