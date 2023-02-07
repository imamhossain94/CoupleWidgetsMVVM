package com.newagedevs.couplewidgets.di

import com.newagedevs.couplewidgets.view.ui.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { MainViewModel(get()) }


}
