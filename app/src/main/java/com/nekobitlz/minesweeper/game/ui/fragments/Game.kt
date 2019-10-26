package com.nekobitlz.minesweeper.game.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nekobitlz.minesweeper.R
import com.nekobitlz.minesweeper.game.enums.CellType
import com.nekobitlz.minesweeper.game.viewmodels.BoardViewModel
import kotlinx.android.synthetic.main.btn_cell.view.*
import kotlinx.android.synthetic.main.game_fragment.*
import kotlin.random.Random

class Game : Fragment() {

    companion object {

        fun newInstance() = Game()
    }

    private lateinit var viewModel: BoardViewModel
    private lateinit var cells: MutableList<MutableList<View>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        cells = mutableListOf()

        val columnCount = viewModel.getColumnCount()
        val rowCount = viewModel.getRowCount()

        gl_board.columnCount = columnCount
        gl_board.rowCount = rowCount

        for (x in 0 until columnCount) {
            val rowCells = mutableListOf<View>()

            for (y in 0 until rowCount) {
                val cellLayout = LayoutInflater.from(this.context).inflate(R.layout.btn_cell, LinearLayout(this.context), true)
                val cellType = viewModel.getCellType(x, y)

                cellLayout.button.setOnClickListener {
                    when (cellType) {
                        CellType.BOMB -> {
                            Snackbar.make(it, "BOOM!!", Snackbar.LENGTH_LONG).show()
                            it.setBackgroundColor(Color.RED)
                        }
                        CellType.EMPTY -> {
                            /* no text */
                            it.setBackgroundColor(Color.BLUE)
                            openEmptyArea(x, y, columnCount, rowCount, cellsCount = 0)
                        }
                        CellType.COVERED -> {
                            it.setBackgroundColor(Color.BLUE)
                            openEmptyArea(x, y, columnCount, rowCount, cellsCount = 0)
                        }
                    }
                }

                gl_board.addView(cellLayout)
                rowCells.add(cellLayout.button)
            }

            cells.add(rowCells)
        }
    }

    private fun openEmptyArea(x: Int, y: Int, width: Int, height: Int, cellsCount: Int) {
        var count = cellsCount
        var currentCell: CellType
        var isOpened: Boolean
        var currentCellButton: Button
        var random = Random(width).nextInt(3, height / 2)

        while (count < random) {
            if (x > 0) {
                currentCell = viewModel.getCellType(x - 1, y) as CellType
                isOpened = viewModel.getCellOpened(x - 1, y)
                currentCellButton = (cells[x - 1][y] as Button)

                if (currentCell.isEmpty() && !isOpened) {
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    viewModel.setCellOpened(x - 1, y)

                    openEmptyArea(x - 1, y, width, height, count)
                }

                if (currentCell.isCovered()) {
                    currentCellButton.text = viewModel.getNearbyCount(x - 1, y)
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    count++
                }
            }

            if (x < width - 1) {
                currentCell = viewModel.getCellType(x + 1, y) as CellType
                isOpened = viewModel.getCellOpened(x + 1, y)
                currentCellButton = (cells[x + 1][y] as Button)

                if (currentCell.isEmpty() && !isOpened) {
                    if (currentCell.isCovered()) {
                        currentCellButton.text = viewModel.getNearbyCount(x + 1, y)
                    }

                    currentCellButton.setBackgroundColor(Color.BLUE)
                    viewModel.setCellOpened(x + 1, y)

                    openEmptyArea(x + 1, y, width, height, count)
                }

                if (currentCell.isCovered()) {
                    currentCellButton.text = viewModel.getNearbyCount(x + 1, y)
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    count++
                }
            }

            if (y > 0) {
                currentCell = viewModel.getCellType(x, y - 1) as CellType
                isOpened = viewModel.getCellOpened(x, y - 1)
                currentCellButton = (cells[x][y - 1] as Button)

                if (currentCell.isEmpty() && !isOpened) {

                    currentCellButton.setBackgroundColor(Color.BLUE)
                    viewModel.setCellOpened(x, y - 1)

                    openEmptyArea(x, y - 1, width, height, count)
                }

                if (currentCell.isCovered()) {
                    currentCellButton.text = viewModel.getNearbyCount(x, y - 1)
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    count++
                }
            }

            if (y < height - 1) {
                currentCell = viewModel.getCellType(x, y + 1) as CellType
                isOpened = viewModel.getCellOpened(x, y + 1)
                currentCellButton = (cells[x][y + 1] as Button)

                if (currentCell.isEmpty() && !isOpened) {
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    viewModel.setCellOpened(x, y + 1)

                    openEmptyArea(x, y + 1, width, height, count)
                }

                if (currentCell.isCovered()) {
                    currentCellButton.text = viewModel.getNearbyCount(x, y + 1)
                    currentCellButton.setBackgroundColor(Color.BLUE)
                    count++
                }
            }

            break
        }
    }
}
