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

    fun getWidgetByAppWidgetId(appWidgetId: Int): Couple? {
        return coupleDao.getWidgetByAppWidgetId(appWidgetId)
    }

    fun getWidgetByAppWidgetIdFlow(appWidgetId: Int): Flow<Couple?> {
        return coupleDao.getWidgetByAppWidgetIdFlow(appWidgetId).flowOn(Dispatchers.IO)
    }

    fun setWidget(couple: Couple): Long {
        val widgets = coupleDao.getActiveWidgets()

        if(widgets.isNotEmpty()) {
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

    fun deleteWidget(id: Long) {
        coupleDao.deleteWidgetById(id)

        // Something must stay active, or the app opens to a blank editor next
        // launch. Promote the first survivor if we just removed the active one.
        if (coupleDao.getActiveWidget() == null) {
            coupleDao.getWidgets().firstOrNull()?.let {
                coupleDao.updateWidgetActiveStatus(it.id, true)
            }
        }
    }

    /**
     * Puts a swipe-deleted widget back exactly as it was, id included, so the
     * home-screen widget it was bound to keeps working after an undo.
     */
    fun restoreWidget(couple: Couple) {
        coupleDao.insertWidget(couple)
    }

    init {
        Timber.d("Injection MainRepository")
    }

}