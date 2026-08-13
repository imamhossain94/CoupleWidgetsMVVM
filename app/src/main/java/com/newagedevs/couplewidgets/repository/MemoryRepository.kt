package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.model.Memory
import com.newagedevs.couplewidgets.persistence.MemoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class MemoryRepository constructor(
    private val memoryDao: MemoryDao
) : Repository {

    fun getMemories(): Flow<List<Memory>> =
        memoryDao.getAllFlow().flowOn(Dispatchers.IO)

    fun getMemoriesOnce(): List<Memory> = memoryDao.getAll()

    fun getMemory(id: Long): Memory? = memoryDao.getById(id)

    /** Inserts or updates, returning the row id. */
    fun save(memory: Memory): Long =
        if (memory.id != 0L && memoryDao.getById(memory.id) != null) {
            memoryDao.update(memory)
            memory.id
        } else {
            memoryDao.insert(memory)
        }

    fun delete(id: Long) = memoryDao.deleteById(id)

    init {
        Timber.d("Injection MemoryRepository")
    }
}
