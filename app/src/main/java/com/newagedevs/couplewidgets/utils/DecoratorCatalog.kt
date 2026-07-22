package com.newagedevs.couplewidgets.utils

import androidx.annotation.DrawableRes
import com.newagedevs.couplewidgets.R

/**
 * The canonical list of frame shapes and heart symbols a user can pick.
 *
 * ## Why decorators are stored by name
 *
 * A chosen decorator used to be persisted as its raw `R.drawable.*` **integer**.
 * Resource IDs are assigned by AAPT in alphabetical order per resource type, so
 * they are NOT stable across builds: adding or removing any drawable shifts every
 * ID that sorts after it. A saved ID then silently resolves to an unrelated
 * drawable — which crashed rendering with "Corrupt XML binary file" once it landed
 * on a PNG, and would otherwise have drawn the wrong shape.
 *
 * Decorators are now persisted by their stable resource **name** (`"shape_4"`),
 * resolved back to a live ID on read by [idFor]. Names are stable against both
 * resource renumbering *and* reordering of the lists below — an index would only
 * survive the former.
 *
 * Legacy rows written before this change hold only a (possibly stale) integer and
 * no name. Their original meaning is unrecoverable, so every read path launders
 * decorators through [safeShape] / [safeSymbol], which degrade an unknown ID to
 * the default instead of crashing.
 */
object DecoratorCatalog {

    /**
     * Resource name paired with its ID. Kept as pairs so a name can never drift
     * away from the drawable it labels.
     */
    private val shapeEntries: List<Pair<String, Int>> = listOf(
        "shape_1" to R.drawable.shape_1,
        "shape_2" to R.drawable.shape_2,
        "shape_3" to R.drawable.shape_3,
        "shape_4" to R.drawable.shape_4,
        "shape_5" to R.drawable.shape_5,
        "shape_6" to R.drawable.shape_6,
        "shape_7" to R.drawable.shape_7,
        "shape_8" to R.drawable.shape_8,
        "shape_9" to R.drawable.shape_9,
        "shape_10" to R.drawable.shape_10,
        "shape_11" to R.drawable.shape_11,
        "shape_12" to R.drawable.shape_12
    )

    private val symbolEntries: List<Pair<String, Int>> = listOf(
        "symbol_1" to R.drawable.symbol_1,
        "symbol_2" to R.drawable.symbol_2,
        "symbol_3" to R.drawable.symbol_3,
        "symbol_4" to R.drawable.symbol_4,
        "symbol_5" to R.drawable.symbol_5,
        "symbol_6" to R.drawable.symbol_6,
        "symbol_7" to R.drawable.symbol_7,
        "symbol_8" to R.drawable.symbol_8,
        "symbol_9" to R.drawable.symbol_9,
        "symbol_10" to R.drawable.symbol_10,
        "symbol_11" to R.drawable.symbol_11,
        "symbol_12" to R.drawable.symbol_12,
        "symbol_13" to R.drawable.symbol_13,
        "symbol_14" to R.drawable.symbol_14
    )

    /** Name -> live resource ID, rebuilt every launch against the current build. */
    private val idsByName: Map<String, Int> =
        (shapeEntries + symbolEntries).toMap()

    /** Live resource ID -> stable name, used when writing. */
    private val namesById: Map<Int, String> =
        (shapeEntries + symbolEntries).associate { (name, id) -> id to name }

    val shapes: List<Int> = shapeEntries.map { it.second }
    val symbols: List<Int> = symbolEntries.map { it.second }

    val shapeTitles: List<String> = listOf(
        "Circle", "Tag", "Hexagon", "Square", "Heart", "Rounded",
        "Star", "Diamond", "Arch", "Shield", "Flower", "Blob"
    )

    val symbolTitles: List<String> = listOf(
        "Heart", "Broken", "Battery", "Heart (Duo)", "Signal", "Bottle", "Heart (Bold)", "Like",
        "Broken Heart", "Two Hearts", "Sparkle", "Infinity", "Ring", "Star"
    )

    @DrawableRes
    val DEFAULT_SHAPE = R.drawable.shape_4

    @DrawableRes
    val DEFAULT_SYMBOL = R.drawable.symbol_6

    /** Stable name to persist for [id], or null if it isn't a catalog decorator. */
    fun nameFor(id: Int?): String? = id?.let { namesById[it] }

    /** Live resource ID for a persisted [name], or null if unknown. */
    @DrawableRes
    fun idFor(name: String?): Int? = name?.let { idsByName[it] }

    /** Returns [id] if it is still a known shape, otherwise the default shape. */
    @DrawableRes
    fun safeShape(id: Int?): Int =
        if (id != null && id in shapes) id else DEFAULT_SHAPE

    /** Returns [id] if it is still a known symbol, otherwise the default symbol. */
    @DrawableRes
    fun safeSymbol(id: Int?): Int =
        if (id != null && id in symbols) id else DEFAULT_SYMBOL
}
