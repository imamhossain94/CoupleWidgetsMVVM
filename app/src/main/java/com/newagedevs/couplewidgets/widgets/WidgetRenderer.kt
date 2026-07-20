package com.newagedevs.couplewidgets.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.extensions.nextAnniversaryText
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

/**
 * Rendering logic shared by [CoupleWidgetProvider] and [CompactCoupleWidgetProvider] — both
 * widget styles show the same [Couple] data, just via different RemoteViews layouts, so the
 * whole render pipeline (data lookup, masking, background/font application) lives here once.
 */
object WidgetRenderer {

    private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val backgroundResources = listOf(
        null, // 0: Transparent (default, matches original pre-customization behavior)
        R.drawable.widget_bg_frosted,
        R.drawable.widget_bg_solid,
        R.drawable.widget_bg_gradient,
    )

    private fun database(context: Context) = CoupleRepository(
        AppDatabase.getInstance(context).coupleDao()
    )

    /** For newly-added widget instances with no saved configuration yet, link them to the active widget. */
    fun linkUnconfiguredWidgets(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            runBlocking(Dispatchers.IO) {
                val existingWidget = database(context).getWidgetByAppWidgetId(appWidgetId)
                if (existingWidget == null) {
                    val activeWidget = database(context).getActiveWidget()
                    if (activeWidget != null) {
                        val newWidget = activeWidget.copy(appWidgetId = appWidgetId)
                        newWidget.id = 0
                        database(context).setWidget(newWidget)
                    }
                }
            }
        }
    }

    fun cleanupDeletedWidgets(context: Context, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach { appWidgetId ->
            runBlocking(Dispatchers.IO) {
                val widget = database(context).getWidgetByAppWidgetId(appWidgetId)
                if (widget != null) {
                    widget.appWidgetId = null
                    database(context).setWidget(widget)
                }
            }
        }
    }

    fun renderCoupleWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        layoutResourceFor: (fontStyle: Int?) -> Int,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            widgetScope.launch {
                // Fetch the specific widget configuration for this appWidgetId directly from DB
                val couple = withContext(Dispatchers.IO) {
                    database(context).getWidgetByAppWidgetId(appWidgetId)
                }

                val defaultDate =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Calendar.getInstance().time)

                // Fallback hierarchy: Specific Mapping -> Active Widget -> Default Couple
                val finalCouple: Couple = couple ?: run {
                    withContext(Dispatchers.IO) {
                        database(context).getActiveWidget()
                    }
                } ?: Couple(
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

                val views = RemoteViews(context.packageName, layoutResourceFor(finalCouple.fontStyle))

                backgroundResources.getOrNull(finalCouple.widgetBackground ?: 0)?.let { bgRes ->
                    views.setInt(R.id.couple_widget, "setBackgroundResource", bgRes)
                }

                setUpClickIntent(context, views, appWidgetId, finalCouple.id)

                views.setTextViewText(R.id.your_name, finalCouple.you?.name)
                views.setTextColor(R.id.your_name, finalCouple.nameColor!!)

                views.setTextViewText(R.id.partner_name, finalCouple.partner?.name)
                views.setTextColor(R.id.partner_name, finalCouple.nameColor)

                views.setTextViewText(
                    R.id.counter_date,
                    dateDifference(finalCouple.inRelation, defaultDate)
                )
                views.setTextColor(R.id.counter_date, finalCouple.counterColor!!)

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
                views.setTextColor(R.id.counter_clock, finalCouple.counterColor)

                val milestoneText = if (finalCouple.showMilestone != false) {
                    nextAnniversaryText(finalCouple.inRelation)
                } else null

                if (milestoneText != null) {
                    views.setTextViewText(R.id.milestone_text, milestoneText)
                    views.setTextColor(R.id.milestone_text, finalCouple.counterColor!!)
                    views.setViewVisibility(R.id.milestone_text, View.VISIBLE)
                } else {
                    views.setViewVisibility(R.id.milestone_text, View.GONE)
                }

                // Render images in parallel
                val job1 = launch { renderCoupleImage(context, views, finalCouple, true) }
                val job2 = launch { renderCoupleImage(context, views, finalCouple, false) }
                joinAll(job1, job2)

                finalCouple.heart?.let { heart ->
                    views.setImageViewResource(R.id.heart_symbol, heart.vector ?: R.drawable.symbol_1)
                    views.setInt(R.id.heart_symbol, "setColorFilter", heart.color ?: Color.WHITE)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
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

    private fun setUpClickIntent(context: Context, views: RemoteViews, appWidgetId: Int, dbWidgetId: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra("widgetId", dbWidgetId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.couple_widget, pendingIntent)
    }
}
