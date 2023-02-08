package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.persistence.AppDatabase
import com.newagedevs.couplewidgets.persistence.CoupleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.koin.core.KoinComponent
import org.koin.core.inject

//class CoupleRepository : KoinComponent {
//
//    private val database: AppDatabase by inject()
//
//    fun getCoupleWithCountersFlow(): Flow<Couple> {
//        return database.coupleDao().getAllCouples().map {
//            it[0]
//        }
//    }
//
//}

class CoupleRepository(private val coupleDao: CoupleDao) {

    fun getCoupleWithFlow(): Flow<Couple> {
        return coupleDao.getAllCouples().map {
            it[0]
        }.flowOn(Dispatchers.IO)
    }

}


