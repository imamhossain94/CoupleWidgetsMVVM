package com.newagedevs.couplewidgets.persistence

import androidx.room.*
import com.newagedevs.couplewidgets.model.Couple
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWidget(shape: Couple): Long

    // GET WIDGETS
    @Query("SELECT * FROM Couple")
    fun getWidgets(): List<Couple>

    @Query("SELECT * FROM Couple")
    fun getWidgetsFlow(): Flow<List<Couple>>

    @Query("SELECT * FROM Couple WHERE id = :id_ LIMIT 1")
    fun getWidgetByID(id_: Long): Couple?


    // GET WIDGETS by Condition
    @Query("SELECT * FROM Couple WHERE active = 1 LIMIT 1")
    fun getActiveWidget(): Couple?

    @Query("SELECT * FROM Couple WHERE active = 1 LIMIT 1")
    fun getActiveWidgetFlow(): Flow<Couple?>

    @Query("SELECT * FROM Couple WHERE active = 1")
    fun getActiveWidgets(): List<Couple>


    // DELETE Widgets
    @Query("DELETE FROM Couple")
    fun deleteWidgets()


    // UPDATE Widgets
    @Update
    fun updateWidget(memo: Couple)

    @Query("UPDATE Couple SET active = :active_ WHERE id = :id_")
    fun updateWidgetActiveStatus(id_: Long, active_: Boolean)
}
