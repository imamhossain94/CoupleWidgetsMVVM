package com.newagedevs.couplewidgets.di

import com.newagedevs.couplewidgets.view.ui.main.MainViewModel
import com.newagedevs.couplewidgets.view.ui.memories.MemoriesViewModel
import com.newagedevs.couplewidgets.view.ui.memories.MemoryEditorViewModel
import com.newagedevs.couplewidgets.view.ui.widgets.WidgetsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (widgetID: Long?, widgetIds:IntArray?, appWidgetID: Int?) -> 
        MainViewModel(widgetID, widgetIds, appWidgetID, get(), get(), get()) 
    }
    viewModel { WidgetsViewModel(get()) }
    viewModel { MemoriesViewModel(get(), get()) }
    viewModel { (memoryId: Long?) -> MemoryEditorViewModel(memoryId, get()) }

}
