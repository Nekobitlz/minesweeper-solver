package com.nekobitlz.minesweeper.game.managers

import com.nekobitlz.minesweeper.game.engine.GameEngine
import com.nekobitlz.minesweeper.game.models.Cell

object CacheManager {

    const val COLUMN_COUNT = 9
    const val ROW_COUNT = 9
    const val BOMBS_COUNT = 10

    private var engine = GameEngine()
    private var cells = engine.getCells()

    fun loadCells(): Array<Array<Cell>> = cells

    fun loadEngine(): GameEngine = engine
}