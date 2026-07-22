package com.newagedevs.couplewidgets.view.ui.memories

import com.newagedevs.couplewidgets.model.Memory
import org.joda.time.LocalDate

/**
 * One row in the memories timeline.
 *
 * Saved events and computed milestones share a list, so they share a type.
 * Milestones aren't editable — only [Event] carries a [Memory] to open.
 */
sealed class MemoryListItem {

    data class Header(val title: String, val subtitle: String?) : MemoryListItem()

    data class Event(
        val memory: Memory,
        val occurrence: LocalDate,
        val daysAway: Int,
    ) : MemoryListItem()

    data class Milestone(
        val label: String,
        val date: LocalDate,
        val daysAway: Int,
        val reached: Boolean,
    ) : MemoryListItem()
}
