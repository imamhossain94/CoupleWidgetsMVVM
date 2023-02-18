package com.newagedevs.couplewidgets.repository

import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.persistence.CoupleDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber


class MainRepository constructor(
    private val coupleDao: CoupleDao
) : Repository {


    fun getWidgets(): Flow<List<Couple>?> {
        return coupleDao.getWidgetsFlow().map {
            it
        }.flowOn(Dispatchers.IO)
    }


    fun getWidgetByID(id: Long): Couple? {
        return coupleDao.getWidgetByID(id)
    }

    fun getActiveWidget(): Couple? {
        return coupleDao.getActiveWidget()
    }

    fun setWidget(couple: Couple): Long {
        val widgets = coupleDao.getActiveWidgets()

        if(widgets.isEmpty()) {
            widgets.forEach {
                coupleDao.updateWidgetActiveStatus(it.id, false)
            }
        }

        return if (getWidgetByID(couple.id) != null) {
            coupleDao.updateWidget(couple)
            couple.id
        } else {
            coupleDao.insertWidget(couple)
        }
    }

    fun deleteAllWidgets() {
        coupleDao.deleteWidgets()
    }

    init {
        Timber.d("Injection MainRepository")
    }

}