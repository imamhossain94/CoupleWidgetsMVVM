package com.newagedevs.couplewidgets.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.extensions.isUriEmpty
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
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
    private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    @ExperimentalCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds: IntArray = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context!!.applicationContext,
                CoupleWidgetProvider::class.java
            )
        )

        val actions = listOf(
            "android.appwidget.action.APPWIDGET_UPDATE",
            "android.intent.action.TIME_SET",
        )

        if (actions.contains(intent!!.action)) {
            renderCoupleWidget(context, appWidgetManager, appWidgetIds)
        }

    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let { startAlarm(it) }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        context?.let { cancelAlarm(it) }
    }

    @ExperimentalCoroutinesApi
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        startAlarm(context)
        renderCoupleWidget(context, appWidgetManager, appWidgetIds)
    }


    private fun renderCoupleWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, layoutResource)
            widgetScope.launch {
                coupleRepository.getCoupleWithFlow().collect { Couples ->
                    val defaultDate =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Calendar.getInstance().time)

                    val couple: Couple = if (Couples.isNotEmpty()) {
                        Couples[0]
                    } else {
                        Couple(
                            id = 0,
                            frame = Decorator(R.drawable.shape_1, Color.WHITE),
                            heart = Decorator(R.drawable.symbol_1, Color.WHITE),
                            nameColor = Color.WHITE,
                            counterColor = Color.WHITE,
                            you = Person("nickname", defaultDate, null),
                            partner = Person("nickname", defaultDate, null),
                            fallInLove = defaultDate,
                            inRelation = defaultDate
                        )
                    }

                    setUpClickIntent(context, views, appWidgetIds)

                    views.setTextViewText(R.id.your_name, couple.you?.name)
                    views.setTextColor(R.id.your_name, couple.nameColor!!)

                    views.setTextViewText(R.id.partner_name, couple.partner?.name)
                    views.setTextColor(R.id.partner_name, couple.nameColor)

                    views.setTextViewText(
                        R.id.counter_date,
                        dateDifference(couple.inRelation, defaultDate)
                    )
                    views.setTextColor(R.id.counter_date, couple.counterColor!!)

                    views.setCharSequence(
                        R.id.counter_clock,
                        "setFormat24Hour",
                        "k'h' mm'm' ss's'"
                    )
                    views.setCharSequence(
                        R.id.counter_clock,
                        "setFormat12Hour",
                        "k'h' mm'm' ss's'"
                    )
                    views.setTextColor(R.id.counter_clock, couple.counterColor)
                    views.setTextColor(R.id.counter_clock, couple.counterColor)


                    renderCoupleImage(context, views, couple, true)
                    renderCoupleImage(context, views, couple, false)

                    views.setImageViewResource(R.id.heart_symbol, couple.heart?.vector!!)
                    views.setInt(R.id.heart_symbol, "setColorFilter", couple.heart?.color!!)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    private suspend fun renderCoupleImage(
        context: Context,
        views: RemoteViews,
        couple: Couple,
        you: Boolean = true
    ) {
        val yourImage = if (isUriEmpty(couple.you?.image)) null else couple.you?.image
        val partnerImage = if (isUriEmpty(couple.partner?.image)) null else couple.partner?.image

        val source = if (you) yourImage else partnerImage
        val destination = if (you) R.id.your_image else R.id.partner_image

        val imageDeferred = CompletableDeferred<Bitmap?>()
        Glide.with(context)
            .asBitmap()
            .load(source ?: R.drawable.ic_person)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageDeferred.complete(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        val bitmapResources = withContext(Dispatchers.IO) {
            imageDeferred.await()
        }

        val image = bitmapResources?.let {
            VectorDrawableMasker.maskImage(
                context,
                it,
                couple.frame?.vector!!,
                200,
                5,
                couple.frame?.color!!
            )
        }
        views.setImageViewBitmap(destination, image)

    }


    private fun setUpClickIntent(context: Context, views: RemoteViews, appWidgetIds: IntArray) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("ids", appWidgetIds)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT or 0
        )
        views.setOnClickPendingIntent(R.id.couple_widget, pendingIntent)
    }

    private fun startAlarm(context: Context) {
        val intent = Intent(context, WidgetAlarmService::class.java)
        context.stopService(intent)
        context.startService(intent)
    }

    private fun cancelAlarm(context: Context) {
        val intent = Intent(context, WidgetAlarmService::class.java)
        context.stopService(intent)
    }

}


