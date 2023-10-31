package com.newagedevs.couplewidgets.view.ui.widgets

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.databinding.Bindable
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.repository.MainRepository
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.newagedevs.couplewidgets.view.ui.main.MainActivity
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


    fun deleteAllWidgets(view: View) {

        CustomSheet().show(view.context) {
            style(SheetStyle.BOTTOM_SHEET)
            title("Confirm Delete")
            content("Are you sure you want to delete all widgets? ")
            onPositive("Yes") {
                mainRepository.deleteAllWidgets()
                toast = "All widgets have been deleted"
                MainActivity.restartActivity(view.context)
            }
        }

    }



    init {
        Timber.d("injection RaceCardViewModel")
    }

}









