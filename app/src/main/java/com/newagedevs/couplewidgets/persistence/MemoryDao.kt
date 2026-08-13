package com.newagedevs.couplewidgets.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.newagedevs.couplewidgets.model.Memory
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memory: Memory): Long

    @Update
    fun update(memory: Memory)

    @Query("SELECT * FROM Memory ORDER BY date ASC")
    fun getAllFlow(): Flow<List<Memory>>

    /**
     * One-shot read. The Flow above doesn't deliver while the timeline is stopped
     * behind the editor, so the screen re-reads on resume.
     */
    @Query("SELECT * FROM Memory ORDER BY date ASC")
    fun getAll(): List<Memory>

    @Query("SELECT * FROM Memory WHERE id = :id_ LIMIT 1")
    fun getById(id_: Long): Memory?

    @Query("DELETE FROM Memory WHERE id = :id_")
    fun deleteById(id_: Long)

    @Query("DELETE FROM Memory")
    fun deleteAll()
}
