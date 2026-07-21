package com.newagedevs.couplewidgets.utils

import androidx.annotation.DrawableRes
import com.newagedevs.couplewidgets.R

/**
 * The canonical list of frame shapes and heart symbols a user can pick.
 *
 * Historically these lists were duplicated inside the pickers, and the chosen
 * `R.drawable.*` **integer** was persisted straight into the database. Resource
 * IDs are assigned by AAPT in alphabetical order per resource type, so they are
 * NOT stable across builds: adding or removing any drawable shifts every ID that
 * sorts after it. A saved ID then silently resolves to an unrelated drawable —
 * which crashed rendering with "Corrupt XML binary file" when it landed on a PNG.
 *
 * Until the stored values are migrated to stable indices, everything that reads a
 * persisted decorator must launder it through [safeShape] / [safeSymbol], so a
 * stale ID degrades to the default shape instead of crashing or drawing garbage.
 */
object DecoratorCatalog {

    @DrawableRes
    val DEFAULT_SHAPE = R.drawable.shape_4

    @DrawableRes
    val DEFAULT_SYMBOL = R.drawable.symbol_6

    val shapes: List<Int> = listOf(
        R.drawable.shape_1, R.drawable.shape_2, R.drawable.shape_3,
        R.drawable.shape_4, R.drawable.shape_5, R.drawable.shape_6,
        R.drawable.shape_7, R.drawable.shape_8, R.drawable.shape_9,
        R.drawable.shape_10, R.drawable.shape_11, R.drawable.shape_12
    )

    val shapeTitles: List<String> = listOf(
        "Circle", "Tag", "Hexagon", "Square", "Heart", "Rounded",
        "Star", "Diamond", "Arch", "Shield", "Flower", "Blob"
    )

    val symbols: List<Int> = listOf(
        R.drawable.symbol_1, R.drawable.symbol_2, R.drawable.symbol_3, R.drawable.symbol_4,
        R.drawable.symbol_5, R.drawable.symbol_6, R.drawable.symbol_7, R.drawable.symbol_8,
        R.drawable.symbol_9, R.drawable.symbol_10, R.drawable.symbol_11,
        R.drawable.symbol_12, R.drawable.symbol_13, R.drawable.symbol_14
    )

    val symbolTitles: List<String> = listOf(
        "Heart", "Broken", "Battery", "Heart (Duo)", "Signal", "Bottle", "Heart (Bold)", "Like",
        "Broken Heart", "Two Hearts", "Sparkle", "Infinity", "Ring", "Star"
    )

    /** Returns [id] if it is still a known shape, otherwise the default shape. */
    @DrawableRes
    fun safeShape(id: Int?): Int =
        if (id != null && id in shapes) id else DEFAULT_SHAPE

    /** Returns [id] if it is still a known symbol, otherwise the default symbol. */
    @DrawableRes
    fun safeSymbol(id: Int?): Int =
        if (id != null && id in symbols) id else DEFAULT_SYMBOL
}
