package com.nekobitlz.minesweeper.game.managers

import androidx.lifecycle.MutableLiveData
import com.nekobitlz.minesweeper.game.engine.Board
import com.nekobitlz.minesweeper.game.extensions.mutableLiveData
import com.nekobitlz.minesweeper.game.models.Cell

object CacheManager {

    const val COLUMN_COUNT = 9
    const val ROW_COUNT = 9
    const val BOMBS_COUNT = 10

    private val board = Board(COLUMN_COUNT, ROW_COUNT, BOMBS_COUNT)
    private val cells = mutableLiveData(board.cells)

    fun loadCells(): MutableLiveData<Array<Array<Cell>>> = cells
}