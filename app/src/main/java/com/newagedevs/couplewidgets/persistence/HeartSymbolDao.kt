package com.newagedevs.couplewidgets.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.newagedevs.couplewidgets.model.HeartSymbol

@Dao
interface HeartSymbolDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertHartSymbols(shape: List<HeartSymbol>)

  @Query("SELECT * FROM HeartSymbol")
  fun getHartSymbols(): List<HeartSymbol>

  @Query("DELETE FROM HeartSymbol")
  fun deleteHartSymbols()
}
