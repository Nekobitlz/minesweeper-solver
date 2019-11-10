package com.nekobitlz.minesweeper.game.repositories

import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.managers.CacheManager

object BoardRepository {

    private val gameEngine = CacheManager.loadEngine()

    fun loadEngine(): GameEngine = gameEngine

    fun getRows(): Int = CacheManager.ROW_COUNT

    fun getColumns(): Int = CacheManager.COLUMN_COUNT
}