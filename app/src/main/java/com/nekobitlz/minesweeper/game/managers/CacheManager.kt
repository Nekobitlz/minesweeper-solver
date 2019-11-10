package com.nekobitlz.minesweeper.game.managers

import com.nekobitlz.minesweeper.game.engine.GameEngine

object CacheManager {

    const val COLUMN_COUNT = 24     /* 9 9 10 - Beginner */
    const val ROW_COUNT = 24        /* 16 16 40 - Intermediate */
    const val BOMBS_COUNT = 99      /* 24 24 99 - Expert */

    private var engine = GameEngine()

    fun loadEngine(): GameEngine = engine
}