package com.newagedevs.couplewidgets.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Memory

@Database(entities = [Couple::class, Memory::class], version = 7, exportSchema = false)
@TypeConverters(PersonConverter::class, DecoratorConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coupleDao(): CoupleDao

    abstract fun memoryDao(): MemoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(com.newagedevs.couplewidgets.R.string.database)
                ).addMigrations(*ALL_MIGRATIONS)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
