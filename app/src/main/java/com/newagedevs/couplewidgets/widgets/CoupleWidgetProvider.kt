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
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import com.newagedevs.couplewidgets.persistence.AppDatabase
import com.newagedevs.couplewidgets.repository.CoupleRepository
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker
import com.newagedevs.couplewidgets.view.ui.main.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class CoupleWidgetProvider : AppWidgetProvider() {

    private val layoutResource: Int get() = R.layout.couple_widget_layout
    private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private fun database(context: Context) = CoupleRepository(
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            context.getString(R.string.database)
        ).build().coupleDao()
    )

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
            "android.appwidget.action.android.appwidget.action.APPWIDGET_UPDATE",
            "android.intent.action.TIME_SET",
        )

        if (actions.contains(intent!!.action)) {
            WidgetAlarmReceiver().setAlarm(context)
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
                database(context).getActiveWidgetFlow().collect {
                    val defaultDate =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Calendar.getInstance().time)

                    val couple: Couple = it
                        ?: Couple(
                            active = true,
                            frame = Decorator(R.drawable.shape_1, Color.WHITE),
                            heart = Decorator(R.drawable.symbol_1, Color.WHITE),
                            nameColor = Color.WHITE,
                            counterColor = Color.WHITE,
                            you = Person("nickname", defaultDate, null),
                            partner = Person("nickname", defaultDate, null),
                            fallInLove = defaultDate,
                            inRelation = defaultDate
                        )

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

        val source = if (you) couple.you?.image else couple.partner?.image
        val destination = if (you) R.id.your_image else R.id.partner_image

        val imageDeferred = CompletableDeferred<Bitmap?>()

        Glide.with(context)
            .asBitmap()
            .load(source)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    imageDeferred.complete(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Glide.with(context)
                        .asBitmap()
                        .load(R.drawable.ic_person)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadCleared(placeholder: Drawable?) {}

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                imageDeferred.complete(resource)
                            }
                        })
                }
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
            putExtra("appWidgetIds", appWidgetIds)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT or 0
        )
        views.setOnClickPendingIntent(R.id.couple_widget, pendingIntent)
    }

    private fun startAlarm(context: Context) {
        WidgetAlarmReceiver().setAlarm(context)
    }

    private fun cancelAlarm(context: Context) {
        WidgetAlarmReceiver().cancelAlarm(context)
    }

}


