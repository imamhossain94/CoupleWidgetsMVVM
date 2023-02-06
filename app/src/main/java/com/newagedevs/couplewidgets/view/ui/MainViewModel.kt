package com.newagedevs.couplewidgets.view.ui

import android.view.View
import androidx.databinding.Bindable
import com.maxkeppeler.sheets.calendar.CalendarMode
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.color.ColorSheet
import com.newagedevs.couplewidgets.model.HeartSymbol
import com.newagedevs.couplewidgets.model.ImageShape
import com.newagedevs.couplewidgets.repository.MainRepository
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import timber.log.Timber


class MainViewModel constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @get:Bindable
    var imageShapeList: List<ImageShape> by bindingProperty(listOf())

    @get:Bindable
    var heartSymbolList: List<HeartSymbol>? by bindingProperty(listOf())


    fun colorPicker(view: View) {

        ColorSheet().show(view.context) {
            title("Background color")
            onPositive { color ->
                // Use color
            }
        }


    }

    fun datePicker(view: View) {

        CalendarSheet().show(view.context) {
            title("What's your date of birth?")
            selectionMode(SelectionMode.DATE)
            onPositive { dateStart, dateEnd ->
                // Handle date or range
            }


        }

    }


    init {
        Timber.d("injection DashboardViewModel")
    }

}


