package com.nekobitlz.minesweeper.game.enums

enum class CellType {

    COVERED,
    BOMB,
    EMPTY;

    fun isBomb() = this == BOMB
}