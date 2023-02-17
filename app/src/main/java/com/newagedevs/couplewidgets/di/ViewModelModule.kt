package com.newagedevs.couplewidgets.di

import com.newagedevs.couplewidgets.view.ui.main.MainViewModel
import com.newagedevs.couplewidgets.view.ui.widgets.WidgetsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { MainViewModel(get()) }
    viewModel { WidgetsViewModel(get()) }

}
