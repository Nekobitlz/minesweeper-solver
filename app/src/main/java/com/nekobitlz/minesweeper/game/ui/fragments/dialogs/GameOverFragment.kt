package com.nekobitlz.minesweeper.game.ui.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.nekobitlz.minesweeper.R
import kotlinx.android.synthetic.main.fragment_game_over.*

class GameOverFragment : DialogFragment() {

    companion object {

        fun newInstance() = GameOverFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_game_over, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_reset.setOnClickListener { closeDialog() }
        btn_back.setOnClickListener {
            this.fragmentManager!!.popBackStack()
            closeDialog()
        }
    }

    private fun closeDialog() {
        this.dialog?.cancel()
    }
}
