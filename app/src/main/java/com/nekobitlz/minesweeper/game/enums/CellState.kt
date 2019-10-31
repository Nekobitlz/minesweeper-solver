package com.nekobitlz.minesweeper.game.enums

enum class CellState {
    OPENED,
    FLAGGED,
    NO_STATE;

    fun isFlagged(): Boolean = this == FLAGGED
}