package com.newagedevs.couplewidgets.di

import com.newagedevs.couplewidgets.repository.MainRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { MainRepository(get()) }

}
