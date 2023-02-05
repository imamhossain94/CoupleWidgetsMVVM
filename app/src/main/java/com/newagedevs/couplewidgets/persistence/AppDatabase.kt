package com.newagedevs.couplewidgets.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.newagedevs.couplewidgets.model.HeartSymbol
import com.newagedevs.couplewidgets.model.ImageShape

@Database(entities = [ImageShape::class, HeartSymbol::class], version = 1, exportSchema = true)
@TypeConverters(value = [])
abstract class AppDatabase : RoomDatabase() {

    abstract fun imageShapeDao(): ImageShapeDao
    abstract fun heartSymbolDao(): HeartSymbolDao

}
