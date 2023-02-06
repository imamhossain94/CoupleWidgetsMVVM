package com.newagedevs.couplewidgets.view.ui

import androidx.databinding.Bindable
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

    init {
        Timber.d("injection DashboardViewModel")
    }

}


