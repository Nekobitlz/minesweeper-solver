@file:Suppress("DEPRECATION")

package com.nekobitlz.minesweeper.game.extensions

import android.graphics.Color
import android.widget.Button
import com.nekobitlz.minesweeper.R
import com.nekobitlz.minesweeper.game.enums.CellType

fun Button.setDefaultStyle() {
    text = ""
    setBackgroundColor(resources.getColor(R.color.colorPrimary))
}

fun Button.setStyleByCellType(cellType: CellType, nearbyCount: String) = when (cellType) {
    CellType.BOMB -> {
        setBackgroundColor(Color.RED)
    }
    CellType.EMPTY -> {
        /* no text */
        setBackgroundColor(resources.getColor(R.color.colorCellOpened))
    }
    CellType.COVERED -> {
        text = nearbyCount
        setBackgroundColor(resources.getColor(R.color.colorCellOpened))
    }
}