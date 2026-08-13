package com.newagedevs.couplewidgets.di

import androidx.room.Room
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.persistence.ALL_MIGRATIONS
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
      // Keep in step with AppDatabase.getInstance() — both builders open the same
      // file, so a migration registered on only one of them would still wipe data.
      .addMigrations(*ALL_MIGRATIONS)
      .fallbackToDestructiveMigration()
      .build()
  }

  single { get<AppDatabase>().coupleDao() }

  single { get<AppDatabase>().memoryDao() }

  single { SharedPref(get()) }

  single { com.newagedevs.couplewidgets.utils.InAppRatingManager(get()) }

}
