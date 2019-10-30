package com.nekobitlz.minesweeper.game.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.repositories.BoardRepository

class BoardViewModel: ViewModel() {

    private val boardRepository = BoardRepository
    private val cells = MutableLiveData<Array<Array<Cell>>>()
    private val engine = MutableLiveData<GameEngine>()

    init {
        cells.value = boardRepository.loadCells()
        engine.value = boardRepository.loadEngine()
    }

    fun getCells(): LiveData<Array<Array<Cell>>> = cells

    fun getColumnCount(): Int = boardRepository.getColumns()

    fun getRowCount(): Int = boardRepository.getRows()

    fun getNearbyCount(x: Int, y: Int): String = cells.value!![x][y].bombsNearby.toString()

    fun handleShortPress(x: Int, y: Int) {
        engine.value!!.handleShortPress(x, y)
        cells.value = engine.value!!.getCells()
    }

    fun reset() {
        engine.value!!.reset()
        cells.value = engine.value!!.getCells()
    }
}