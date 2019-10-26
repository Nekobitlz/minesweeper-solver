package com.nekobitlz.minesweeper

import com.nekobitlz.minesweeper.game.engine.Board
import org.junit.Test

import org.junit.Assert.*

class ExampleUnitTest {

    @Test
    fun bombCount() {
        val board = Board(500, 500, 2500)

        board.cells.forEach {
            println()
            it.forEach {
                cell ->
                print("${cell.cellType.toString()} ")
            }
        }

        board.cells.forEach {
            println()
            it.forEach {
                    cell ->
                print("${cell.bombsNearby } ")
            }
        }
    }
}
