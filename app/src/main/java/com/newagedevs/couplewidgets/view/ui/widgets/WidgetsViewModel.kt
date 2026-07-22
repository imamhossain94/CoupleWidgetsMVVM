package com.newagedevs.couplewidgets.view.ui.widgets

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.databinding.Bindable
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.repository.MainRepository
import com.newagedevs.couplewidgets.view.ui.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.asBindingProperty
import com.skydoves.bindables.bindingProperty
import timber.log.Timber


class WidgetsViewModel constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @VisibleForTesting
    internal val posterListFlow = mainRepository.getWidgets()

    @get:Bindable
    val widgets: List<Couple>? by posterListFlow.asBindingProperty(null)


    /**
     * Removes a single widget. The row is kept by the caller so an undo can put
     * it back with the same id, which matters because a home-screen widget may
     * still be bound to it.
     */
    fun deleteWidget(couple: Couple) {
        mainRepository.deleteWidget(couple.id)
    }

    fun restoreWidget(couple: Couple) {
        mainRepository.restoreWidget(couple)
    }

    fun deleteAllWidgets(view: View) {
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete all widgets?")
            .setPositiveButton("Yes") { _, _ ->
                mainRepository.deleteAllWidgets()
                toast = "All widgets have been deleted"
                MainActivity.restartActivity(view.context)
            }
            .setNegativeButton("No", null)
            .show()
    }



    init {
        Timber.d("injection RaceCardViewModel")
    }

}









