package com.newagedevs.couplewidgets.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newagedevs.couplewidgets.model.Couple

@Database(entities = [Couple::class], version = 2, exportSchema = true)
@TypeConverters(value = [DecoratorConverter::class, PersonConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun coupleDao(): CoupleDao

}
