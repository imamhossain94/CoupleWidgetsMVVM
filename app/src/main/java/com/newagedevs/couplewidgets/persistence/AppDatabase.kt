package com.newagedevs.couplewidgets.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newagedevs.couplewidgets.model.Couple

@Database(entities = [Couple::class], version = 2, exportSchema = true)
@TypeConverters(value = [DecoratorConverter::class, PersonConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun coupleDao(): CoupleDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    context.getString(com.newagedevs.couplewidgets.R.string.database)
                ).build().also { instance = it }
            }
        }
    }

}
