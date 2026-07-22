package com.newagedevs.couplewidgets.utils

import androidx.annotation.LayoutRes
import com.newagedevs.couplewidgets.R

/**
 * The fonts a user can pick for the widget.
 *
 * RemoteViews has no API to set a Typeface or fontFamily at runtime, so each font
 * must be baked into its own layout variant via `android:fontFamily`. The families
 * here are all built-in system families, guaranteed present on every device, so no
 * font files ship with the app.
 *
 * [fontStyle] is persisted as the **index** into this list. Reorder with care —
 * changing the order remaps saved widgets.
 */
object WidgetFontCatalog {

    data class WidgetFont(
        val label: String,
        /** Built-in system family, used for the in-app preview via Typeface.create(). */
        val family: String,
        /** Layout variant that hard-codes this family for the RemoteViews widget. */
        @LayoutRes val layout: Int,
    )

    val fonts: List<WidgetFont> = listOf(
        WidgetFont("Default", "sans-serif", R.layout.couple_widget_layout),
        WidgetFont("Serif", "serif", R.layout.couple_widget_layout_serif),
        WidgetFont("Cursive", "cursive", R.layout.couple_widget_layout_cursive),
        WidgetFont("Monospace", "monospace", R.layout.couple_widget_layout_monospace),
        WidgetFont("Light", "sans-serif-light", R.layout.couple_widget_layout_light),
        WidgetFont("Condensed", "sans-serif-condensed", R.layout.couple_widget_layout_condensed),
        WidgetFont("Medium", "sans-serif-medium", R.layout.couple_widget_layout_medium),
        WidgetFont("Black", "sans-serif-black", R.layout.couple_widget_layout_black),
    )

    val titles: List<String> get() = fonts.map { it.label }

    @LayoutRes
    fun layoutFor(index: Int?): Int = fonts.getOrNull(index ?: 0)?.layout ?: fonts[0].layout

    fun familyFor(index: Int?): String = fonts.getOrNull(index ?: 0)?.family ?: fonts[0].family

    fun labelFor(index: Int?): String = fonts.getOrNull(index ?: 0)?.label ?: fonts[0].label
}
