package com.newagedevs.couplewidgets.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.newagedevs.couplewidgets.model.Couple

@Dao
interface CoupleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCouple(shape: Couple)

    @Query("SELECT * FROM Couple WHERE id = :id_")
    fun getCouple(id_: Long): Couple

    @Query("SELECT * FROM Couple")
    fun getCouples(): List<Couple>

    @Query("DELETE FROM Couple")
    fun deleteCouple()
}
