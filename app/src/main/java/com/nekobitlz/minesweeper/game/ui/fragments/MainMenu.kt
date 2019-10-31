package com.nekobitlz.minesweeper.game.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nekobitlz.minesweeper.R
import kotlinx.android.synthetic.main.fragment_main_menu.*

class MainMenu : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_main_menu, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_play.setOnClickListener {

            if (savedInstanceState == null) {
               activity!!.supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_container, Game.newInstance(), GAME_FRAGMENT)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    companion object {
        private const val GAME_FRAGMENT = "GAME_FRAGMENT"

        fun newInstance() = MainMenu()
    }
}
