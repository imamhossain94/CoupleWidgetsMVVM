package com.newagedevs.couplewidgets.view.ui.memories

import androidx.databinding.Bindable
import com.newagedevs.couplewidgets.model.Memory
import com.newagedevs.couplewidgets.repository.MemoryRepository
import com.newagedevs.couplewidgets.utils.EventIconCatalog
import com.newagedevs.couplewidgets.utils.MilestoneCalculator
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import org.joda.time.LocalDate
import timber.log.Timber

class MemoryEditorViewModel constructor(
    private val memoryId: Long?,
    private val memoryRepository: MemoryRepository,
) : BindingViewModel() {

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @get:Bindable
    var title: String? by bindingProperty("")

    @get:Bindable
    var note: String? by bindingProperty("")

    @get:Bindable
    var date: String by bindingProperty(MilestoneCalculator.format(LocalDate.now()))
        private set

    @get:Bindable
    var iconName: String by bindingProperty(EventIconCatalog.DEFAULT_NAME)
        private set

    @get:Bindable
    var iconRes: Int by bindingProperty(EventIconCatalog.resFor(EventIconCatalog.DEFAULT_NAME))
        private set

    @get:Bindable
    var iconLabel: String by bindingProperty(EventIconCatalog.labelFor(EventIconCatalog.DEFAULT_NAME))
        private set

    @get:Bindable
    var repeatsYearly: Boolean by bindingProperty(false)

    @get:Bindable
    var isEditing: Boolean by bindingProperty(false)
        private set

    /** Set once the editor is opened on an existing memory. */
    private var existing: Memory? = null

    init {
        Timber.d("injection MemoryEditorViewModel")
        load()
    }

    private fun load() {
        val id = memoryId ?: return
        val memory = memoryRepository.getMemory(id) ?: return

        existing = memory
        isEditing = true
        title = memory.title
        note = memory.note
        date = memory.date
        repeatsYearly = memory.repeatsYearly
        setIcon(memory.iconName ?: EventIconCatalog.DEFAULT_NAME)
    }

    fun setIcon(name: String) {
        iconName = name
        iconRes = EventIconCatalog.resFor(name)
        iconLabel = EventIconCatalog.labelFor(name)
    }

    fun setDate(value: LocalDate) {
        date = MilestoneCalculator.format(value)
    }

    fun currentDate(): LocalDate =
        MilestoneCalculator.parse(date) ?: LocalDate.now()

    /** Returns true when the memory was saved; false means validation failed. */
    fun save(): Boolean {
        val name = title?.trim()
        if (name.isNullOrEmpty()) {
            toast = "Give this memory a name"
            return false
        }

        val memory = Memory(
            title = name,
            date = date,
            iconName = iconName,
            note = note?.trim()?.takeIf { it.isNotEmpty() },
            repeatsYearly = repeatsYearly,
        ).apply { id = existing?.id ?: 0L }

        memoryRepository.save(memory)
        return true
    }

    fun delete(): Boolean {
        val id = existing?.id ?: return false
        memoryRepository.delete(id)
        return true
    }
}
