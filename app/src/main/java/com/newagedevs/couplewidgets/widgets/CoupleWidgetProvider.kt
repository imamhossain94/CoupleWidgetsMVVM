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
import java.util.concurrent.CountDownLatch


class CoupleWidgetProvider : AppWidgetProvider(), KoinComponent {

    private val coupleRepository: CoupleRepository by inject()
    private val layoutResource: Int get() = R.layout.couple_widget_layout


    @ExperimentalCoroutinesApi
    override fun onReceive(context: Context?, intent: Intent?) {

        val actions = listOf(
            "android.intent.action.TIME_SET",
            "android.intent.action.TIMEZONE_CHANGED",
            "android.intent.action.DATE_CHANGED"
        )

        if (actions.contains(intent!!.action)) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds: IntArray = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context!!.applicationContext,
                    CoupleWidgetProvider::class.java
                )
            )

            renderCoupleWidget(context, appWidgetManager, appWidgetIds)
        }

        super.onReceive(context, intent)
    }


    @ExperimentalCoroutinesApi
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) = renderCoupleWidget(context, appWidgetManager, appWidgetIds)

    @OptIn(DelicateCoroutinesApi::class)
    private fun renderCoupleWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, layoutResource)

            GlobalScope.launch(Dispatchers.Main) {
                coupleRepository.getCoupleWithFlow().collect { Couples ->
                    withContext(Dispatchers.Main) {


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
    }

    private suspend fun renderCoupleImage(
        context: Context,
        views: RemoteViews,
        couple: Couple,
        you: Boolean = true
    ) {

        val latch = CountDownLatch(1)

        val source = if (you) couple.you?.image else couple.partner?.image
        val destination = if (you) R.id.your_image else R.id.partner_image

        Glide.with(context)
            .asBitmap()
            .load(source ?: R.drawable.ic_person)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val image = VectorDrawableMasker.maskImage(
                        context,
                        resource,
                        couple.frame?.vector!!,
                        200,
                        5,
                        couple.frame?.color!!
                    )
                    views.setImageViewBitmap(destination, image)

                    latch.countDown()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        withContext(Dispatchers.IO) {
            latch.await()
        }

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


}