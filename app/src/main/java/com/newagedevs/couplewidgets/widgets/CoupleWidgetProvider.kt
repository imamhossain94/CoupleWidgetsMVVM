package com.newagedevs.couplewidgets.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import com.newagedevs.couplewidgets.persistence.AppDatabase
import com.newagedevs.couplewidgets.repository.CoupleRepository
import com.newagedevs.couplewidgets.utils.DecoratorCatalog
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker
import com.newagedevs.couplewidgets.utils.WidgetFontCatalog
import com.newagedevs.couplewidgets.view.ui.main.MainActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class CoupleWidgetProvider : AppWidgetProvider() {

    private val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private companion object {
        /** Square avatar resolution. Kept modest so two bitmaps plus the rest stay
         *  well under the RemoteViews transaction size limit. */
        const val AVATAR_SIZE_PX = 240
        /** Border stroke width in avatar pixels. */
        const val BORDER_PX = 6
    }

    private fun database(context: Context) = CoupleRepository(
        AppDatabase.getInstance(context).coupleDao()
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
            AppWidgetManager.ACTION_APPWIDGET_UPDATE,
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
        // Clean up widget mappings
        appWidgetIds?.forEach { appWidgetId ->
            runBlocking(Dispatchers.IO) {
                val widget = database(context!!).getWidgetByAppWidgetId(appWidgetId)
                if (widget != null) {
                    widget.appWidgetId = null
                    database(context).setWidget(widget)
                }
            }
        }
        context?.let { cancelAlarm(it) }
    }

    @ExperimentalCoroutinesApi
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // For each widget being updated, if it doesn't have a configuration, link to current active one
        appWidgetIds.forEach { appWidgetId ->
            runBlocking(Dispatchers.IO) {
                val existingWidget = database(context).getWidgetByAppWidgetId(appWidgetId)
                if (existingWidget == null) {
                    val activeWidget = database(context).getActiveWidget()
                    if (activeWidget != null) {
                        // Clone active widget for this specific appWidgetId
                        val newWidget = activeWidget.copy(appWidgetId = appWidgetId)
                        newWidget.id = 0 // Force new insertion
                        database(context).setWidget(newWidget)
                    }
                }
            }
        }

        renderCoupleWidget(context, appWidgetManager, appWidgetIds)
    }


    private fun renderCoupleWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
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

                val views = RemoteViews(
                    context.packageName,
                    WidgetFontCatalog.layoutFor(finalCouple.fontStyle)
                )

                applyBackground(context, views, finalCouple)

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

                // Build both avatars off the RemoteViews in parallel, then apply them
                // sequentially. RemoteViews is NOT thread-safe: mutating the same
                // instance from two coroutines dropped one image and corrupted borders.
                val youAvatar = async { buildAvatar(context, finalCouple, you = true) }
                val partnerAvatar = async { buildAvatar(context, finalCouple, you = false) }
                youAvatar.await()?.let { views.setImageViewBitmap(R.id.your_image, it) }
                partnerAvatar.await()?.let { views.setImageViewBitmap(R.id.partner_image, it) }

                finalCouple.heart?.let { heart ->
                    views.setImageViewResource(
                        R.id.heart_symbol,
                        DecoratorCatalog.safeSymbol(heart.vector)
                    )
                    views.setInt(R.id.heart_symbol, "setColorFilter", heart.color ?: Color.WHITE)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    /** Loads one person's photo and masks it into the chosen frame shape. */
    private suspend fun buildAvatar(context: Context, couple: Couple, you: Boolean): Bitmap {
        val source = if (you) couple.you?.image else couple.partner?.image
        val bitmap = loadBitmap(context, source)
        return VectorDrawableMasker.maskImage(
            context,
            bitmap,
            couple.frame?.vector ?: DecoratorCatalog.DEFAULT_SHAPE,
            AVATAR_SIZE_PX,
            BORDER_PX,
            couple.frame?.color ?: Color.WHITE
        )
    }

    /**
     * Loads a bounded, square bitmap synchronously on the IO dispatcher.
     *
     * Uses Glide's blocking [com.bumptech.glide.RequestBuilder.submit] instead of a
     * CustomTarget + CompletableDeferred: the old callback approach could hang forever
     * if onLoadCleared fired (the deferred never completed), which stalled the whole
     * widget update. Any failure — including a null/empty photo — falls back to the
     * placeholder person icon.
     */
    private suspend fun loadBitmap(context: Context, source: Uri?): Bitmap =
        withContext(Dispatchers.IO) {
            runCatching {
                Glide.with(context).asBitmap()
                    .load(source?.takeIf { it != Uri.EMPTY })
                    .override(AVATAR_SIZE_PX, AVATAR_SIZE_PX)
                    .centerCrop()
                    .submit()
                    .get()
            }.getOrElse {
                Glide.with(context).asBitmap()
                    .load(R.drawable.ic_person)
                    .override(AVATAR_SIZE_PX, AVATAR_SIZE_PX)
                    .fitCenter()
                    .submit()
                    .get()
            }
        }


    /**
     * Paints the widget background. Index 0 (None) leaves the freshly-inflated
     * RemoteViews with no background. Frosted uses a fixed drawable; Solid uses the
     * rounded drawable tinted with the user's chosen color.
     *
     * RemoteViews can only tint a background via setColorStateList (API 31+); on
     * older devices we fall back to a flat color, which loses the rounded corners.
     */
    private fun applyBackground(context: Context, views: RemoteViews, couple: Couple) {
        when (couple.widgetBackground ?: 0) {
            1 -> views.setInt(
                R.id.couple_widget, "setBackgroundResource", R.drawable.widget_bg_frosted
            )
            2 -> {
                val color = couple.widgetBackgroundColor
                    ?: ContextCompat.getColor(context, R.color.love_rose_deep)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    views.setInt(
                        R.id.couple_widget, "setBackgroundResource", R.drawable.widget_bg_solid
                    )
                    views.setColorStateList(
                        R.id.couple_widget,
                        "setBackgroundTintList",
                        ColorStateList.valueOf(color)
                    )
                } else {
                    views.setInt(R.id.couple_widget, "setBackgroundColor", color)
                }
            }
            // 0 (None): nothing to draw.
        }
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

    private fun startAlarm(context: Context) {
        WidgetAlarmReceiver().setAlarm(context)
    }

    private fun cancelAlarm(context: Context) {
        WidgetAlarmReceiver().cancelAlarm(context)
    }

}
