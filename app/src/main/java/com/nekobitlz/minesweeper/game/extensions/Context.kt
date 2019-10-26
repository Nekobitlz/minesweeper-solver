package com.nekobitlz.minesweeper.game.extensions

import android.content.Context
import android.util.TypedValue

fun Context.convertDpToPx(dp: Float): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    resources.displayMetrics
)

fun Context.convertPxToDp(px: Float): Float = px / resources.displayMetrics.density