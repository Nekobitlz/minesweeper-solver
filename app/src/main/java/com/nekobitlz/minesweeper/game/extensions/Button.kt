package com.nekobitlz.minesweeper.game.extensions

import android.widget.Button
import com.nekobitlz.minesweeper.R


@Suppress("DEPRECATION")
fun Button.setDefaultStyle() {
    text = ""
    setBackgroundColor(resources.getColor(R.color.colorPrimary))
}