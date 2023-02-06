package com.newagedevs.couplewidgets.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newagedevs.couplewidgets.model.Couple

@Database(entities = [Couple::class], version = 1, exportSchema = true)
@TypeConverters(value = [])
abstract class AppDatabase : RoomDatabase() {

    abstract fun coupleDao(): CoupleDao

}
