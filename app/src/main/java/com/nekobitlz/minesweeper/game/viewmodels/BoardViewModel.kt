package com.nekobitlz.minesweeper.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.repositories.BoardRepository

class BoardViewModel: ViewModel() {

    private val boardRepository = BoardRepository
    private val cells = boardRepository.loadCells()

    fun getCells(): LiveData<Array<Array<Cell>>> = cells

    fun getColumnCount(): Int = boardRepository.getColumns()

    fun getRowCount(): Int = boardRepository.getRows()

    fun getCellType(x: Int, y: Int): Any = cells.value!![x][y].cellType

    fun getNearbyCount(x: Int, y: Int): String = cells.value!![x][y].bombsNearby.toString()

    fun getCellOpened(x: Int, y: Int): Boolean = cells.value!![x][y].isOpened

    fun setCellOpened(x: Int, y: Int) {
        cells.value!![x][y].isOpened = true
    }
}