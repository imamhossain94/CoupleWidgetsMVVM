package com.newagedevs.couplewidgets.view.ui.widgets

import androidx.databinding.Bindable
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.repository.MainRepository
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import timber.log.Timber


class WidgetsViewModel constructor(
    mainRepository: MainRepository
) : BindingViewModel() {

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @get:Bindable
    var widgets: List<Couple>? by bindingProperty(null)
        private set

    init {
        Timber.d("injection RaceCardViewModel")
        widgets = mainRepository.getCouples()
    }

}









