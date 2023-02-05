package com.newagedevs.couplewidgets.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.newagedevs.couplewidgets.model.ImageShape

@Dao
interface ImageShapeDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertImageShapes(shape: List<ImageShape>)

  @Query("SELECT * FROM ImageShape")
  fun getImageShapes(): List<ImageShape>

  @Query("DELETE FROM ImageShape")
  fun deleteImageShapes()
}
