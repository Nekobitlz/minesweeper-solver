package com.nekobitlz.minesweeper.game.enums

enum class CellType {

    COVERED,
    BOMB,
    EMPTY;

    fun isBomb() = this == BOMB

    fun isCovered() = this == COVERED

    fun isEmpty() = this == EMPTY
}