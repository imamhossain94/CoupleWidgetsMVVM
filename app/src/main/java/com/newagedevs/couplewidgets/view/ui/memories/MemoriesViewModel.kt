package com.newagedevs.couplewidgets.view.ui.memories

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.newagedevs.couplewidgets.model.Memory
import com.newagedevs.couplewidgets.repository.MainRepository
import com.newagedevs.couplewidgets.repository.MemoryRepository
import com.newagedevs.couplewidgets.utils.MilestoneCalculator
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import timber.log.Timber

class MemoriesViewModel constructor(
    private val memoryRepository: MemoryRepository,
    private val mainRepository: MainRepository,
) : BindingViewModel() {

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @get:Bindable
    var items: List<MemoryListItem> by bindingProperty(emptyList())
        private set

    /** "5 years, 2 months and 3 days" — the headline on the hero card. */
    @get:Bindable
    var togetherLabel: String by bindingProperty("")
        private set

    @get:Bindable
    var togetherDaysLabel: String by bindingProperty("")
        private set

    @get:Bindable
    var nextUpLabel: String by bindingProperty("")
        private set

    @get:Bindable
    var isEmpty: Boolean by bindingProperty(false)
        private set

    /** Relationship start date, read from the active widget. */
    private var startDate: LocalDate? = null

    init {
        Timber.d("injection MemoriesViewModel")
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            val couple = withContext(Dispatchers.IO) { mainRepository.getActiveWidget() }
            startDate = MilestoneCalculator.parse(couple?.inRelation)

            memoryRepository.getMemories().collectLatest { memories ->
                rebuild(memories)
            }
        }
    }

    /**
     * Re-reads everything and rebuilds the timeline.
     *
     * Called from `onResume` because two things can change while this screen is
     * stopped: the user may add or edit a memory in the editor, and the
     * relationship date lives on the main screen. The Room Flow above keeps the
     * list live in the foreground but doesn't deliver to a stopped screen, so
     * this is what makes a returning user see their new memory.
     */
    fun refresh() {
        viewModelScope.launch {
            val (couple, memories) = withContext(Dispatchers.IO) {
                mainRepository.getActiveWidget() to memoryRepository.getMemoriesOnce()
            }
            startDate = MilestoneCalculator.parse(couple?.inRelation)
            rebuild(memories)
        }
    }

    private fun rebuild(memories: List<Memory>) {
        val today = LocalDate.now()
        val start = startDate

        // ---- Hero summary -------------------------------------------------
        if (start != null) {
            val t = MilestoneCalculator.togetherness(start, today)
            togetherLabel = buildString {
                if (t.years > 0) append("${t.years}y ")
                if (t.months > 0) append("${t.months}m ")
                append("${t.remainingDays}d")
            }.trim()
            togetherDaysLabel = "${t.days} days together"

            val next = MilestoneCalculator.nextMilestone(start, today)
            nextUpLabel = next?.let {
                "${it.label} · ${MilestoneCalculator.relativeLabel(it.daysAway)}"
            } ?: "Every day is a milestone"
        } else {
            togetherLabel = "—"
            togetherDaysLabel = "Set your relationship date"
            nextUpLabel = "Add it in Dates & Milestones on the main screen"
        }

        // ---- Events, split by whether they are still ahead ------------------
        val events = memories.map { memory ->
            val date = MilestoneCalculator.parse(memory.date) ?: today
            val occurrence =
                MilestoneCalculator.nextOccurrence(date, memory.repeatsYearly, today)
            MemoryListItem.Event(
                memory = memory,
                occurrence = occurrence,
                daysAway = MilestoneCalculator.daysUntil(occurrence, today)
            )
        }

        val upcomingEvents = events.filter { it.daysAway >= 0 }.sortedBy { it.daysAway }
        val pastEvents = events.filter { it.daysAway < 0 }.sortedByDescending { it.occurrence }

        // ---- Milestones ----------------------------------------------------
        val upcomingMilestones = start?.let { s ->
            MilestoneCalculator.milestones(s, today)
                .filter { !it.reached }
                .take(4)
                .map {
                    MemoryListItem.Milestone(it.label, it.date, it.daysAway, reached = false)
                }
        }.orEmpty()

        val reachedMilestones = start?.let { s ->
            MilestoneCalculator.milestones(s, today)
                .filter { it.reached }
                .takeLast(3)
                .reversed()
                .map {
                    MemoryListItem.Milestone(it.label, it.date, it.daysAway, reached = true)
                }
        }.orEmpty()

        // ---- Assemble ------------------------------------------------------
        val built = buildList {
            if (upcomingEvents.isNotEmpty()) {
                add(MemoryListItem.Header("Coming up", "Your next moments together"))
                addAll(upcomingEvents)
            }
            if (upcomingMilestones.isNotEmpty()) {
                add(MemoryListItem.Header("Milestones ahead", "Counted from your first day"))
                addAll(upcomingMilestones)
            }
            if (reachedMilestones.isNotEmpty()) {
                add(MemoryListItem.Header("Already celebrated", null))
                addAll(reachedMilestones)
            }
            if (pastEvents.isNotEmpty()) {
                add(MemoryListItem.Header("Memories", "Moments you've saved"))
                addAll(pastEvents)
            }
        }

        items = built
        isEmpty = memories.isEmpty() && built.none { it is MemoryListItem.Milestone }
    }

    fun delete(memory: Memory) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryRepository.delete(memory.id)
        }
    }

    /** Undo a swipe-delete, keeping the original id so nothing else shifts. */
    fun restore(memory: Memory) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryRepository.save(memory)
        }
    }
}
