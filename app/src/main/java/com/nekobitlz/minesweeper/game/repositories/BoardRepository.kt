package com.nekobitlz.minesweeper.game.repositories

import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.managers.CacheManager
import com.nekobitlz.minesweeper.game.models.Cell

object BoardRepository {

    private val cells = CacheManager.loadCells()
    private val gameEngine = CacheManager.loadEngine()

    fun loadCells(): Array<Array<Cell>> = cells

    fun loadEngine(): GameEngine = gameEngine

    fun getRows(): Int = CacheManager.ROW_COUNT

    fun getColumns(): Int = CacheManager.COLUMN_COUNT
}