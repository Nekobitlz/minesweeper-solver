package com.nekobitlz.minesweeper.game.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nekobitlz.minesweeper.R
import com.nekobitlz.minesweeper.game.models.Cell
import com.nekobitlz.minesweeper.game.ui.fragments.Game
import com.nekobitlz.minesweeper.game.ui.fragments.MainMenu
import com.nekobitlz.minesweeper.game.viewmodels.BoardViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_container, MainMenu.newInstance(), MAIN_MENU_FRAGMENT)
                .commit()
        }
    }

    companion object {
        private const val MAIN_MENU_FRAGMENT = "MAIN_MENU_FRAGMENT"
    }
}
