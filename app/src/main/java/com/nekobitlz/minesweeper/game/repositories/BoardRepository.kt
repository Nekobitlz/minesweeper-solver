package com.nekobitlz.minesweeper.game.repositories

import androidx.lifecycle.MutableLiveData
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell

object BoardRepository {

    private val cells = CacheManager.loadCells()

    fun loadCells(): MutableLiveData<Array<Array<Cell>>> = cells

    fun getRows(): Int = CacheManager.ROW_COUNT

    fun getColumns(): Int = CacheManager.COLUMN_COUNT
}