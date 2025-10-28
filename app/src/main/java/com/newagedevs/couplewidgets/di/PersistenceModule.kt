package com.newagedevs.couplewidgets.di

import androidx.room.Room
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.persistence.AppDatabase
import com.newagedevs.couplewidgets.persistence.SharedPref
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val persistenceModule = module {

  single {
    Room
      .databaseBuilder(
        androidApplication(),
        AppDatabase::class.java,
        androidApplication().getString(R.string.database)
      )
      .allowMainThreadQueries()
      .fallbackToDestructiveMigration()
      .build()
  }

  single { get<AppDatabase>().coupleDao() }

  single { SharedPref(get()) }

}
