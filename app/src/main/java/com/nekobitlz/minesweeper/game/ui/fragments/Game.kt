@file:Suppress("DEPRECATION")

package com.nekobitlz.minesweeper.game.ui.fragments

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
import com.nekobitlz.minesweeper.game.enums.CellState.*
import com.nekobitlz.minesweeper.game.enums.GameState
import com.nekobitlz.minesweeper.game.enums.GameState.GAME_OVER
import com.nekobitlz.minesweeper.game.enums.GameState.WIN
import com.nekobitlz.minesweeper.game.extensions.setDefaultStyle
import com.nekobitlz.minesweeper.game.extensions.setStyleByCellType
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.ui.fragments.dialogs.GameOverFragment
import com.nekobitlz.minesweeper.game.ui.fragments.dialogs.WinningFragment
import com.nekobitlz.minesweeper.game.viewmodels.BoardViewModel
import kotlinx.android.synthetic.main.btn_cell.view.*
import kotlinx.android.synthetic.main.game_fragment.*

class Game : Fragment() {

    companion object {
        private const val GAME_OVER_FRAGMENT = "GAME_OVER_FRAGMENT"
        private const val WINNING_FRAGMENT = "WINNING_FRAGMENT"

        fun newInstance() = Game()
    }

    private lateinit var viewModel: BoardViewModel
    private lateinit var cellButtons: MutableList<MutableList<Button>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.game_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        viewModel.getCells().observe(this, Observer { updateUI(it) })
        viewModel.getGameState().observe(this, Observer { updateGameState(it) })
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

                cellLayout.button.setOnClickListener { viewModel.handleShortPress(x, y) }
                cellLayout.button.setOnLongClickListener { viewModel.handleLongPress(x, y) }

                gl_board.addView(cellLayout)
                rowCells.add(cellLayout.button)
            }

            cellButtons.add(rowCells)
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

    private fun updateButton(cell: Cell) {
        val x = cell.x
        val y = cell.y

        val button = cellButtons[x][y]
        val cellType = cell.cellType

        button.apply {
            when (cell.cellState) {
                OPENED -> setStyleByCellType(cellType, x, y, nearbyCount = viewModel.getNearbyCount(x, y))
                FLAGGED -> setBackgroundColor(resources.getColor(R.color.colorCellFlagged))
                NO_STATE -> setDefaultStyle()
            }
        }
    }

    private fun resetButtons() {
        val width = cellButtons.size
        val height = cellButtons[0].size

        for (x in 0 until width) {
            for (y in 0 until height) {
                val button = cellButtons[x][y]
                button.setDefaultStyle()
            }
        }
    }

    private fun updateGameState(gameState: GameState) {
        when (gameState) {
            GAME_OVER -> showGameOverFragment()
            WIN -> showWinningFragment()
            else -> { /* enjoy game */ }
        }
    }

    private fun showGameOverFragment() {
        val gameOverFragment = GameOverFragment.newInstance()
        gameOverFragment.isCancelable = false
        gameOverFragment.show(this.fragmentManager!!, GAME_OVER_FRAGMENT)
    }

    private fun showWinningFragment() {
        val winningFragment = WinningFragment.newInstance()
        winningFragment.isCancelable = false
        winningFragment.show(this.fragmentManager!!, WINNING_FRAGMENT)
    }
}