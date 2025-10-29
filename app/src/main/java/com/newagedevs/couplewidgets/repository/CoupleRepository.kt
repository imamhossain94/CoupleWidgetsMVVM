package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.persistence.CoupleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CoupleRepository(private val coupleDao: CoupleDao) {

    fun getActiveWidgetFlow(): Flow<Couple?> {
        return coupleDao.getActiveWidgetFlow().map {
            it
        }.flowOn(Dispatchers.IO)
    }

}