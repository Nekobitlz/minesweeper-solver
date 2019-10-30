package com.nekobitlz.minesweeper.game.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nekobitlz.minesweeper.R
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.viewmodels.BoardViewModel
import kotlinx.android.synthetic.main.btn_cell.view.*
import kotlinx.android.synthetic.main.game_fragment.*

class Game : Fragment() {

    companion object {

        fun newInstance() = Game()
    }

    private lateinit var viewModel: BoardViewModel
    private lateinit var cellButtons: MutableList<MutableList<Button>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    private fun initViews() {
        cellButtons = mutableListOf()

        val columnCount = viewModel.getColumnCount()
        val rowCount = viewModel.getRowCount()

        gl_board.columnCount = columnCount
        gl_board.rowCount = rowCount

        btn_reset.setOnClickListener {
            viewModel.reset()
            resetButtons()
        }

        for (x in 0 until columnCount) {
            val rowCells = mutableListOf<Button>()

            for (y in 0 until rowCount) {
                val cellLayout = LayoutInflater.from(this.context)
                    .inflate(R.layout.btn_cell, LinearLayout(this.context), true)

                cellLayout.button.setOnClickListener {
                    viewModel.handleShortPress(x, y)
                }

                gl_board.addView(cellLayout)
                rowCells.add(cellLayout.button)
            }

            cellButtons.add(rowCells)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        viewModel.getCells().observe(this, Observer { updateUI(it) })
    }

    private fun updateButton(cell: Cell) {
        val x = cell.x
        val y = cell.y

        val button = cellButtons[x][y]
        val cellType = cell.cellType

        when {
            cellType.isBomb() && cell.isOpened -> {
                button.setBackgroundColor(Color.RED)
            }
            cellType.isEmpty() && cell.isOpened -> {
                /* no text */
                button.setBackgroundColor(Color.BLACK)
            }
            cellType.isCovered() && cell.isOpened -> {
                button.text = viewModel.getNearbyCount(x, y)
                button.setBackgroundColor(Color.BLACK)
            }
            else -> {
                button.text = ""
                button.setBackgroundColor(Color.CYAN)
            }
        }
    }

    private fun updateUI(cells: Array<Array<Cell>>?) {
        if (cells != null) {
            val width = cells.size
            val height = cells[0].size

            for (x in 0 until width) {
                for (y in 0 until height) {
                    updateButton(cells[x][y])
                }
            }
        }
    }

    private fun resetButtons() {
        val width = cellButtons.size
        val height = cellButtons[0].size

        for (x in 0 until width) {
            for (y in 0 until height) {
                val button = cellButtons[x][y]

                button.text = ""
                button.setBackgroundColor(Color.CYAN)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.reset()
    }
}
