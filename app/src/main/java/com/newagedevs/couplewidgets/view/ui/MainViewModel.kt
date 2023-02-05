package com.newagedevs.couplewidgets.view.ui

import androidx.databinding.Bindable
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


    init {
        Timber.d("injection DashboardViewModel")
    }

}


