package com.newagedevs.couplewidgets.repository

import android.graphics.Color
import android.net.Uri
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
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

    /**
     * Seeds one active widget with sensible defaults on first launch, so the app
     * opens to a populated preview and the Memories screen (which reads the active
     * widget's relationship date) has something to show. No-op once any widget exists.
     */
    fun ensureDefaultWidget() {
        if (coupleDao.countWidgets() > 0) return
        coupleDao.insertWidget(defaultCouple())
    }

    private fun defaultCouple(): Couple = Couple(
        active = true,
        frame = Decorator(R.drawable.shape_4, Color.WHITE),
        heart = Decorator(R.drawable.symbol_6, Color.WHITE),
        nameColor = Color.WHITE,
        counterColor = Color.WHITE,
        you = Person("You", "1997-12-07", Uri.EMPTY),
        partner = Person("Partner", "2004-10-21", Uri.EMPTY),
        fallInLove = "2021-05-27",
        inRelation = "2021-05-27",
        widgetBackground = 0,
        fontStyle = 0,
    )

    /**
     * Pushes a just-saved configuration onto every widget currently on the home
     * screen so the change shows immediately.
     *
     * The app keeps a separate DB row per placed appWidgetId (see
     * [CoupleWidgetProvider.onUpdate]); editing from the app icon only touches the
     * active row, leaving the placed rows stale until the next system refresh. This
     * syncs them. The row already saved by [setWidget] ([savedId]) is skipped.
     *
     * Trade-off: all placed widgets end up showing the latest saved look, i.e. the
     * app treats them as one shared configuration rather than independent widgets.
     */
    fun updatePlacedWidgets(config: Couple, savedId: Long, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val existing = coupleDao.getWidgetByAppWidgetId(appWidgetId)
            if (existing != null && existing.id == savedId) return@forEach

            val row = config.copy(active = false, appWidgetId = appWidgetId)
            row.id = existing?.id ?: 0L
            coupleDao.insertWidget(row)
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