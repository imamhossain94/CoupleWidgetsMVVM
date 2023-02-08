package com.newagedevs.couplewidgets.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.RemoteViews
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.repository.CoupleRepository
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker
import com.newagedevs.couplewidgets.view.ui.MainActivity
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.SimpleDateFormat
import java.util.*

class CoupleWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val coupleRepository: CoupleRepository by inject()
    private val layoutResource: Int get() = R.layout.couple_widget_layout

    @ExperimentalCoroutinesApi
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, layoutResource)

            GlobalScope.launch(Dispatchers.Main) {
                coupleRepository.getCoupleWithFlow().collect { couple ->
                    withContext(Dispatchers.Main) {
                        setUpClickIntent(context, views, couple)

                        views.setTextViewText(R.id.your_name, couple.you?.name)
                        views.setTextViewText(R.id.partner_name, couple.partner?.name)

                        val defaultDate =
                            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                        views.setTextViewText(R.id.counter_date, dateDifference(couple.inRelation, defaultDate))

                        views.setCharSequence(R.id.counter_clock, "setFormat24Hour", "k'h' mm'm' ss's'")
                        views.setCharSequence(R.id.counter_clock, "setFormat12Hour", "k'h' mm'm' ss's'")


                        val yourImage = VectorDrawableMasker.maskImage(
                            context,
                            couple.you?.image!!,
                            couple.frame?.vector!!,
                            100,
                            5
                        )

                        val partnerImage = VectorDrawableMasker.maskImage(
                            context,
                            couple.partner?.image!!,
                            couple.frame?.vector!!,
                            100,
                            5
                        )

                        views.setImageViewBitmap(R.id.your_image,  yourImage)
                        views.setImageViewBitmap(R.id.partner_image, partnerImage)

                        views.setImageViewResource(R.id.heart_symbol, couple.heart?.vector!!)

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }

    private fun setUpClickIntent(context: Context, views: RemoteViews, couple: Couple) {
        val intent = Intent(context, MainActivity::class.java).apply {
            //putExtra(CoupleDatabase.COUPLE_ID, coupleWithCounters.couple.id)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT or 0)
        views.setOnClickPendingIntent(R.id.couple_widget, pendingIntent)
    }



}