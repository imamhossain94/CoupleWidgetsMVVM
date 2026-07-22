package com.newagedevs.couplewidgets.view.ui.memories

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.calendar.utils.toLocalDate
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMemoryEditorBinding
import com.newagedevs.couplewidgets.extensions.parseCalendarFromString
import com.newagedevs.couplewidgets.utils.EventIconCatalog
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.intentOf
import org.joda.time.LocalDate
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class MemoryEditorActivity :
    BindingActivity<ActivityMemoryEditorBinding>(R.layout.activity_memory_editor) {

    private lateinit var viewModel: MemoryEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val memoryId = intent.getLongExtra(EXTRA_MEMORY_ID, -1L).takeIf { it >= 0 }
        viewModel = getViewModel { parametersOf(memoryId) }

        binding {
            dispatcher = this@MemoryEditorActivity
            vm = viewModel
        }

        binding.fieldDate.setOnClickListener { pickDate() }
        binding.fieldIcon.setOnClickListener { pickIcon() }

        binding.saveMemory.setOnClickListener {
            if (viewModel.save()) finish()
        }

        binding.deleteMemory.setOnClickListener { confirmDelete() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun pickDate() {
        CalendarSheet().show(this) {
            title("When did it happen?")
            selectionMode(SelectionMode.DATE)
            setSelectedDate(parseCalendarFromString(viewModel.date))
            onPositive { dateStart, _ ->
                @SuppressLint("RestrictedApi")
                val picked = dateStart.toLocalDate()
                viewModel.setDate(LocalDate(picked.year, picked.monthValue, picked.dayOfMonth))
            }
        }
    }

    private fun pickIcon() {
        val icons = EventIconCatalog.icons
        OptionSheet().show(this) {
            title("Choose an icon")
            with(*icons.map { Option(it.res, it.label) }.toTypedArray())
            onPositive { index: Int, _: Option ->
                viewModel.setIcon(icons[index].name)
            }
        }
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete memory")
            .setMessage("Are you sure you want to delete this memory?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val EXTRA_MEMORY_ID = "memoryId"

        /** Pass a null [memoryId] to create a new memory. */
        fun startActivity(context: Context, memoryId: Long?) =
            context.intentOf<MemoryEditorActivity> {
                putExtra(EXTRA_MEMORY_ID, memoryId ?: -1L)
                context.startActivity(intent, null)
            }
    }
}
