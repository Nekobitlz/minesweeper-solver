package com.nekobitlz.minesweeper.game.models

import androidx.annotation.VisibleForTesting
import com.nekobitlz.minesweeper.game.enums.CellType

class Cell(val x: Int, val y: Int) {
    var cellType = CellType.EMPTY
    var isOpened = false
    var bombsNearby = 0

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override fun toString(): String {
        return "Cell(x=$x, y=$y, cellType=$cellType, bombsNearby=$bombsNearby)"
    }
}