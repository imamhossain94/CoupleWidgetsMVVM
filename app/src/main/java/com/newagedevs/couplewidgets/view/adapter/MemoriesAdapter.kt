package com.newagedevs.couplewidgets.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.model.Memory
import com.newagedevs.couplewidgets.utils.EventIconCatalog
import com.newagedevs.couplewidgets.utils.MilestoneCalculator
import com.newagedevs.couplewidgets.view.ui.memories.MemoryListItem
import org.joda.time.format.DateTimeFormat

class MemoriesAdapter(
    private val onEventClick: (Memory) -> Unit
) : ListAdapter<MemoryListItem, RecyclerView.ViewHolder>(DIFF) {

    private val prettyDate = DateTimeFormat.forPattern("d MMM yyyy")

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is MemoryListItem.Header -> TYPE_HEADER
        is MemoryListItem.Event -> TYPE_EVENT
        is MemoryListItem.Milestone -> TYPE_MILESTONE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderHolder(inflater.inflate(R.layout.item_memory_header, parent, false))
            TYPE_EVENT -> EventHolder(inflater.inflate(R.layout.item_memory, parent, false))
            else -> MilestoneHolder(inflater.inflate(R.layout.item_milestone, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MemoryListItem.Header -> (holder as HeaderHolder).bind(item)
            is MemoryListItem.Event -> (holder as EventHolder).bind(item)
            is MemoryListItem.Milestone -> (holder as MilestoneHolder).bind(item)
        }
    }

    /** Null unless the row is a saved (editable, swipeable) event. */
    fun memoryAt(position: Int): Memory? =
        (getItemOrNull(position) as? MemoryListItem.Event)?.memory

    private fun getItemOrNull(position: Int): MemoryListItem? =
        if (position in 0 until itemCount) getItem(position) else null

    inner class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.header_title)
        private val subtitle: TextView = view.findViewById(R.id.header_subtitle)

        fun bind(item: MemoryListItem.Header) {
            title.text = item.title
            subtitle.text = item.subtitle
            subtitle.visibility = if (item.subtitle.isNullOrBlank()) View.GONE else View.VISIBLE
        }
    }

    inner class EventHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.memory_icon)
        private val title: TextView = view.findViewById(R.id.memory_title)
        private val date: TextView = view.findViewById(R.id.memory_date)
        private val countdown: TextView = view.findViewById(R.id.memory_countdown)
        private val note: TextView = view.findViewById(R.id.memory_note)

        fun bind(item: MemoryListItem.Event) {
            val memory = item.memory
            icon.setImageResource(EventIconCatalog.resFor(memory.iconName))
            title.text = memory.title
            date.text = item.occurrence.toString(prettyDate)
            countdown.text = MilestoneCalculator.relativeLabel(item.daysAway)

            note.text = memory.note
            note.visibility = if (memory.note.isNullOrBlank()) View.GONE else View.VISIBLE

            // Today's events get the accent treatment so they stand out.
            val accent = if (item.daysAway == 0) R.color.love_rose else R.color.love_orchid
            countdown.setTextColor(ContextCompat.getColor(countdown.context, accent))

            itemView.setOnClickListener { onEventClick(memory) }
        }
    }

    inner class MilestoneHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val label: TextView = view.findViewById(R.id.milestone_label)
        private val date: TextView = view.findViewById(R.id.milestone_date)
        private val countdown: TextView = view.findViewById(R.id.milestone_countdown)
        private val icon: ImageView = view.findViewById(R.id.milestone_icon)

        fun bind(item: MemoryListItem.Milestone) {
            label.text = item.label
            date.text = item.date.toString(prettyDate)
            countdown.text = MilestoneCalculator.relativeLabel(item.daysAway)
            icon.setImageResource(
                if (item.reached) R.drawable.ic_check else R.drawable.ic_sparkle
            )
            itemView.alpha = if (item.reached) 0.6f else 1f
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_EVENT = 1
        private const val TYPE_MILESTONE = 2

        private val DIFF = object : DiffUtil.ItemCallback<MemoryListItem>() {
            override fun areItemsTheSame(a: MemoryListItem, b: MemoryListItem): Boolean =
                when {
                    a is MemoryListItem.Event && b is MemoryListItem.Event ->
                        a.memory.id == b.memory.id
                    a is MemoryListItem.Header && b is MemoryListItem.Header ->
                        a.title == b.title
                    a is MemoryListItem.Milestone && b is MemoryListItem.Milestone ->
                        a.label == b.label
                    else -> false
                }

            override fun areContentsTheSame(a: MemoryListItem, b: MemoryListItem): Boolean = a == b
        }
    }
}
