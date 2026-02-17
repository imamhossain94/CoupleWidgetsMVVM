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

    fun getWidgetByIDFlow(id: Long): Flow<Couple?> {
        return coupleDao.getWidgetByIDFlow(id).map {
            it
        }.flowOn(Dispatchers.IO)
    }

    fun getActiveWidget(): Couple? {
        return coupleDao.getActiveWidget()
    }

    fun getWidgetByAppWidgetId(appWidgetId: Int): Couple? {
        return coupleDao.getWidgetByAppWidgetId(appWidgetId)
    }

    fun getWidgetByAppWidgetIdFlow(appWidgetId: Int): Flow<Couple?> {
        return coupleDao.getWidgetByAppWidgetIdFlow(appWidgetId).flowOn(Dispatchers.IO)
    }

    fun setWidget(couple: Couple): Long {
        val widgets = coupleDao.getActiveWidgets()

        if (widgets.isNotEmpty()) {
            widgets.forEach {
                coupleDao.updateWidgetActiveStatus(it.id, false)
            }
        }

        return if (coupleDao.getWidgetByID(couple.id) != null) {
            coupleDao.updateWidget(couple)
            couple.id
        } else {
            coupleDao.insertWidget(couple)
        }
    }

}