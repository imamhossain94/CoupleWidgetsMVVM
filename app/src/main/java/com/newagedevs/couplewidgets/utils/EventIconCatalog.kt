package com.newagedevs.couplewidgets.utils

import androidx.annotation.DrawableRes
import com.newagedevs.couplewidgets.R

/**
 * Icons a user can attach to a saved memory.
 *
 * Stored by stable **name**, never by resource ID — IDs shift whenever drawables
 * are added or removed. Same rule, and same reasoning, as [DecoratorCatalog].
 */
object EventIconCatalog {

    const val DEFAULT_NAME = "heart"

    /** Stable key, display label, and the drawable it maps to in this build. */
    data class EventIcon(val name: String, val label: String, @DrawableRes val res: Int)

    val icons: List<EventIcon> = listOf(
        EventIcon("heart", "Love", R.drawable.ic_heart),
        EventIcon("ring", "Engagement", R.drawable.ic_ring),
        EventIcon("birthday", "Birthday", R.drawable.ic_birthday),
        EventIcon("gift", "Gift", R.drawable.ic_gift),
        EventIcon("plane", "Trip", R.drawable.ic_plane),
        EventIcon("home", "Home", R.drawable.ic_home_heart),
        EventIcon("star", "Special", R.drawable.ic_star),
        EventIcon("camera", "Photo", R.drawable.ic_camera),
        EventIcon("sparkle", "Milestone", R.drawable.ic_sparkle),
        EventIcon("calendar", "Date", R.drawable.ic_calender),
    )

    private val byName: Map<String, EventIcon> = icons.associateBy { it.name }

    /** Drawable for a stored [name], falling back to the default if unknown. */
    @DrawableRes
    fun resFor(name: String?): Int =
        byName[name ?: DEFAULT_NAME]?.res ?: byName.getValue(DEFAULT_NAME).res

    fun labelFor(name: String?): String =
        byName[name ?: DEFAULT_NAME]?.label ?: byName.getValue(DEFAULT_NAME).label

    fun indexOf(name: String?): Int =
        icons.indexOfFirst { it.name == name }.takeIf { it >= 0 } ?: 0
}
