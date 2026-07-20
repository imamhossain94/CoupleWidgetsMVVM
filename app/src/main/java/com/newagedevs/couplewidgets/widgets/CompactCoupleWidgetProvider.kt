package com.newagedevs.couplewidgets.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.newagedevs.couplewidgets.R
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * A smaller alternate widget style showing the same [com.newagedevs.couplewidgets.model.Couple]
 * data as [CoupleWidgetProvider], via [WidgetRenderer]. Font-style customization only applies
 * to the main widget (no room for it at this size), so this always renders the default layout.
 */
class CompactCoupleWidgetProvider : AppWidgetProvider() {

    private fun layoutResourceFor(@Suppress("UNUSED_PARAMETER") fontStyle: Int?): Int =
        R.layout.compact_widget_layout

    @ExperimentalCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds: IntArray = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context!!.applicationContext,
                CompactCoupleWidgetProvider::class.java
            )
        )

        val actions = listOf(
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
            "android.intent.action.TIME_SET",
        )

        if (actions.contains(intent!!.action)) {
            WidgetAlarmReceiver().setAlarm(context)
            WidgetRenderer.renderCoupleWidget(context, appWidgetManager, appWidgetIds, ::layoutResourceFor)
        }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let { WidgetAlarmReceiver().setAlarm(it) }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        context?.let {
            WidgetRenderer.cleanupDeletedWidgets(it, appWidgetIds)
            WidgetAlarmReceiver().cancelAlarm(it)
        }
    }

    @ExperimentalCoroutinesApi
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        WidgetRenderer.linkUnconfiguredWidgets(context, appWidgetIds)

        WidgetRenderer.renderCoupleWidget(context, appWidgetManager, appWidgetIds, ::layoutResourceFor)
    }

}
