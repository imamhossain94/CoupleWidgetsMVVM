package com.newagedevs.couplewidgets.view.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.Bindable
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.github.dhaval2404.imagepicker.ImagePicker
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.calendar.utils.toLocalDate
import com.maxkeppeler.sheets.color.ColorSheet
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.option.DisplayMode
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.*
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import com.newagedevs.couplewidgets.persistence.SharedPref
import androidx.lifecycle.viewModelScope
import com.newagedevs.couplewidgets.repository.MainRepository
import com.newagedevs.couplewidgets.utils.Constants
import com.newagedevs.couplewidgets.utils.DecoratorCatalog
import com.newagedevs.couplewidgets.utils.WidgetFontCatalog
import com.newagedevs.couplewidgets.utils.InAppRatingManager
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.newagedevs.couplewidgets.view.ui.widgets.WidgetsActivity
import com.newagedevs.couplewidgets.widgets.CoupleWidgetProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(
    private var widgetId: Long?,
    private var widgetIds: IntArray?,
    private var appWidgetId: Int?,
    private val mainRepository: MainRepository,
    val preference: SharedPref,
    private val inAppRatingManager: InAppRatingManager
) : BindingViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val defaultDate = dateFormat.format(Calendar.getInstance().time)
    private val imuDate = "1997-12-07"
    private val ufmDate = "2004-10-21"
    private val fallDate = "2021-05-27"
    private val relationDate = "2021-05-27"

    private val widgetBackgroundTitles = listOf("None", "Frosted", "Solid")
    private val widgetBackgroundIcons = listOf(R.drawable.ic_circle, R.drawable.widget_bg_frosted, R.drawable.widget_bg_solid)
    private val fontStyleTitles = WidgetFontCatalog.titles

    /** Index of the "Solid" background, used to reveal the color picker. */
    private val solidBackgroundIndex = 2
    private val defaultBackgroundColor = Color.parseColor("#E8386A")

    @get:Bindable
    var toast: String? by bindingProperty(null)

    @get:Bindable
    var yourName: String? by bindingProperty("imu")

    @get:Bindable
    var partnerName: String? by bindingProperty("ufm")

    @get:Bindable
    var yourImage: Uri? by bindingProperty(Uri.EMPTY)

    @get:Bindable
    var partnerImage: Uri? by bindingProperty(Uri.EMPTY)

    @get:Bindable
    var shape: Int? by bindingProperty(R.drawable.shape_4)

    @get:Bindable
    var shapeColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var symbol: Int? by bindingProperty(R.drawable.symbol_6)

    @get:Bindable
    var symbolColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var nameColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var counterColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var fallInLove: String? by bindingProperty(fallDate)

    @get:Bindable
    var inRelation: String? by bindingProperty(relationDate)

    @get:Bindable
    var yourBirthday: String? by bindingProperty(imuDate)

    @get:Bindable
    var partnerBirthday: String? by bindingProperty(ufmDate)

    @get:Bindable
    var counterDate: String? by bindingProperty(defaultDate)

    @get:Bindable
    var widgetBackground: Int? by bindingProperty(0)

    @get:Bindable
    var widgetBackgroundLabel: String? by bindingProperty("None")

    @get:Bindable
    var widgetBackgroundIcon: Int? by bindingProperty(R.drawable.ic_circle)

    @get:Bindable
    var widgetBackgroundColor: Int? by bindingProperty(Color.parseColor("#E8386A"))

    /** Drives visibility of the solid-color field: true only when "Solid" is chosen. */
    @get:Bindable
    var showBackgroundColor: Boolean by bindingProperty(false)
        private set

    @get:Bindable
    var fontStyle: Int? by bindingProperty(0)

    @get:Bindable
    var fontStyleLabel: String? by bindingProperty("Default")

    // Nullable: AdMob's interstitial must be discarded after showing and reloaded
    var interstitialAd: InterstitialAd? = null

    /**
     * Single place for the gate -> show -> record -> reload sequence, used by both the
     * nav-to-widgets click (MainActivity) and the create-widget flow (submitData below) so
     * ad-cadence tuning only needs to happen here.
     */
    fun showInterstitialIfEligible(activity: Activity, onShown: () -> Unit = {}): Boolean {
        val ad = interstitialAd ?: return false
        if (!preference.shouldShowInterstitialAds()) return false

        ad.show(activity)
        interstitialAd = null
        preference.recordInterstitialAdShown()
        onShown()
        return true
    }

    init {
        viewModelScope.launch {
            initializeData()
        }
    }

    fun refreshData(newWidgetId: Long?, newAppWidgetId: Int?) {
        this.widgetId = newWidgetId
        this.appWidgetId = newAppWidgetId
        viewModelScope.launch {
            initializeData()
        }
    }

    // Widget settings
    fun shapePicker(view: View) {
        val shapes = DecoratorCatalog.shapes
        val shapeTitles = DecoratorCatalog.shapeTitles

        OptionSheet().show(view.context) {
            title("Select image shape")
            displayMode(DisplayMode.GRID_VERTICAL)
            columns(3)
            with(
                Option(shapes[0], shapeTitles[0]),
                Option(shapes[1], shapeTitles[1]),
                Option(shapes[2], shapeTitles[2]),
                Option(shapes[3], shapeTitles[3]),
                Option(shapes[4], shapeTitles[4]),
                Option(shapes[5], shapeTitles[5]),
                Option(shapes[6], shapeTitles[6]),
                Option(shapes[7], shapeTitles[7]),
                Option(shapes[8], shapeTitles[8]),
                Option(shapes[9], shapeTitles[9]),
                Option(shapes[10], shapeTitles[10]),
                Option(shapes[11], shapeTitles[11]),
            )
            onPositive { index: Int, _: Option ->

                val textView = view as TextView

                val image = ResourcesCompat.getDrawable(resources, shapes[index], null)
                image?.setBounds(0, 0, 24.px, 24.px)

                textView.text = shapeTitles[index]
                textView.setCompoundDrawables(image, null, null, null)

                shape = shapes[index]
            }
        }


    }

    fun symbolPicker(view: View) {
        val symbols = DecoratorCatalog.symbols
        val symbolTitles = DecoratorCatalog.symbolTitles

        OptionSheet().show(view.context) {
            title("Select heart symbol")
            displayMode(DisplayMode.GRID_VERTICAL)
            columns(3)
            with(
                Option(symbols[0], symbolTitles[0]),
                Option(symbols[1], symbolTitles[1]),
                Option(symbols[2], symbolTitles[2]),
                Option(symbols[3], symbolTitles[3]),
                Option(symbols[4], symbolTitles[4]),
                Option(symbols[5], symbolTitles[5]),
                Option(symbols[6], symbolTitles[6]),
                Option(symbols[7], symbolTitles[7]),
                Option(symbols[8], symbolTitles[8]),
                Option(symbols[9], symbolTitles[9]),
                Option(symbols[10], symbolTitles[10]),
                Option(symbols[11], symbolTitles[11]),
                Option(symbols[12], symbolTitles[12]),
                Option(symbols[13], symbolTitles[13]),
            )
            onPositive { index: Int, _: Option ->

                val textView = view as TextView

                val image = ResourcesCompat.getDrawable(resources, symbols[index], null)
                image?.setBounds(0, 0, 24.px, 24.px)

                textView.text = symbolTitles[index]
                textView.setCompoundDrawables(image, null, null, null)

                symbol = symbols[index]
            }
        }


    }

    fun backgroundPicker(view: View) {
        OptionSheet().show(view.context) {
            title("Select widget background")
            with(
                Option(widgetBackgroundIcons[0], widgetBackgroundTitles[0]),
                Option(widgetBackgroundIcons[1], widgetBackgroundTitles[1]),
                Option(widgetBackgroundIcons[2], widgetBackgroundTitles[2]),
            )
            onPositive { index: Int, _: Option ->
                widgetBackground = index
                widgetBackgroundLabel = widgetBackgroundTitles[index]
                widgetBackgroundIcon = widgetBackgroundIcons[index]
                showBackgroundColor = index == solidBackgroundIndex
            }
        }
    }

    fun fontPicker(view: View) {
        val fontTitles = WidgetFontCatalog.titles
        OptionSheet().show(view.context) {
            title("Select widget font")
            displayMode(DisplayMode.GRID_VERTICAL)
            columns(3)
            with(*fontTitles.map { Option(R.drawable.ic_brush, it) }.toTypedArray())
            onPositive { index: Int, _: Option ->
                fontStyle = index
                fontStyleLabel = fontTitles[index]
            }
        }
    }

    fun colorPicker(view: View) {

        val textView = view as TextView
        val tag = textView.tag

        ColorSheet().show(view.context) {
            title("Select ${tag.toString().lowercase()}")
            onPositive { color ->
//              Use color
//              val hexColor = "#${Integer.toHexString(color).uppercase()}"
//              textView.text = hexColor
//              textView.setTextColor(color)
//              textView.compoundDrawables[0].setTint(color)

                when (tag) {
                    "Shape Color" -> {
                        shapeColor = color
                    }
                    "Symbol Color" -> {
                        symbolColor = color
                    }
                    "Counter Color" -> {
                        counterColor = color
                    }
                    "Name Color" -> {
                        nameColor = color
                    }
                    "Background Color" -> {
                        widgetBackgroundColor = color
                    }
                }

            }
        }


    }

    // Couple details
    fun imagePicker(view: View) {

        val textView = view as TextView

        val requestCode = if (textView.tag == "Your image") 1094 else 1095
        val title =
            if (textView.tag == "Your image") "Choose your image" else "Choose your partner image"

        OptionSheet().show(view.context) {
            title(title)
            with(
                Option(R.drawable.ic_camera, "Camera"),
                Option(R.drawable.ic_picture, "Gallery")
            )
            onPositive { index: Int, _: Option ->

                val wrapper = ContextWrapper(view.context)
//                val file = wrapper.getDir("images", Context.MODE_PRIVATE)
                val file = wrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!

                if (index == 0) {
                    ImagePicker.with(view.context as Activity)
                        .cameraOnly()
                        .cropSquare()
                        .saveDir(file)
                        .start(requestCode)
                } else if (index == 1) {
                    ImagePicker.with(view.context as Activity)
                        .galleryOnly()
                        .cropSquare()
                        .saveDir(file)
                        .start(requestCode)
                }
            }
        }

    }

    // Date and time
    fun datePicker(view: View) {

        val textView = view as TextView
        val tag = textView.tag

        var title = ""
        var calendar: Calendar = Calendar.getInstance()
        when (tag) {
            "Fall in Love" -> {
                title = "What's the date you fell in love?"
                calendar = parseCalendarFromString(fallInLove!!)
            }
            "In Relation" -> {
                title = "When did your relationship start?"
                calendar = parseCalendarFromString(inRelation!!)
            }
            "Your Birthday" -> {
                title = "What's your date of birth?"
                calendar = parseCalendarFromString(yourBirthday!!)
            }
            "Partner Birthday" -> {
                title = "What's your partner's birthday?"
                calendar = parseCalendarFromString(partnerBirthday!!)
            }
        }

        CalendarSheet().show(view.context) {
            title(title)
            selectionMode(SelectionMode.DATE)
            setSelectedDate(calendar)
            onPositive { dateStart, _ ->

                @SuppressLint("RestrictedApi")
                val date = dateStart.toLocalDate().toString()

                when (tag) {
                    "Fall in Love" -> {
                        fallInLove = date
                    }
                    "In Relation" -> {
                        inRelation = date
                        counterDate = dateDifference(inRelation, defaultDate)
                    }
                    "Your Birthday" -> {
                        yourBirthday = date
                    }
                    "Partner Birthday" -> {
                        partnerBirthday = date
                    }
                }

            }
        }

    }


    fun submitData(view: View) {

        // Small tap-feedback pulse on the save button.
        view.animate()
            .scaleX(1.2f).scaleY(1.2f)
            .setDuration(120L)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(120L).start()
            }
            .start()

        val couple = Couple(
            active = true,
            frame = Decorator(shape, shapeColor),
            heart = Decorator(symbol, symbolColor),
            nameColor = nameColor,
            counterColor = counterColor,
            you = Person(yourName, yourBirthday, yourImage),
            partner = Person(partnerName, partnerBirthday, partnerImage),
            fallInLove = fallInLove,
            inRelation = inRelation,
            widgetBackground = widgetBackground,
            widgetBackgroundColor = widgetBackgroundColor,
            fontStyle = fontStyle
        )

        // Update widget
        val context = view.context
        val activity = view.context as Activity

        val intent = Intent(context, CoupleWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

        val id = activity.intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, CoupleWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

        MaterialAlertDialogBuilder(context)
            .setTitle("Confirm Changes")
            .setMessage("Do you want to save the changes or create a new widget?")
            .setPositiveButton("Save Changes") { dialog, which ->
                toast = "Changes saved successfully"

                if (widgetId != null) {
                    couple.id = widgetId!!
                }

                couple.appWidgetId = appWidgetId
                widgetId = mainRepository.setWidget(couple)

                // Sync the change onto every placed home-screen widget so it
                // updates immediately, not just on the next system refresh.
                mainRepository.updatePlacedWidgets(couple, widgetId!!, ids)

                if (id != null) {
                    context.sendBroadcast(intent)
                    activity.setResult(RESULT_OK, intent)
                    activity.finish()
                    inAppRatingManager.onActionCompleted(activity)
                } else {
                    context.sendBroadcast(intent)
                    // If no widget on home screen, guide user to add one
                    if (!hasWidgetOnHomeScreen(context)) {
                        guideUserToAddWidget(activity)
                    } else if (widgetIds == null) {
                        activity.finish()
                        inAppRatingManager.onActionCompleted(activity)
                    }
                }
            }
            .setNegativeButton("Create Widget") { dialog, which ->
                toast = "New widget created"
                widgetIds = null
                couple.appWidgetId = appWidgetId
                widgetId = mainRepository.setWidget(couple)

                viewModelScope.launch {
                    initializeData()
                    context.sendBroadcast(intent)
                }

                val shouldShowAd = showInterstitialIfEligible(activity)

                // If no widget on home screen, guide user to add one
                if (!hasWidgetOnHomeScreen(context) && !shouldShowAd) {
                    guideUserToAddWidget(activity)
                }

                inAppRatingManager.onActionCompleted(activity)
            }
            .setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()

    }

    private suspend fun initializeData() {

        var couple:Couple? = null

        withContext(Dispatchers.IO) {
            // Priority 1: If we have a specific widgetId (DB ID)
            if (widgetId != null) {
                couple = mainRepository.getWidgetByID(widgetId!!)
            }
            // Priority 2: If we have an appWidgetId (System ID from native home screen)
            else if (appWidgetId != null) {
                couple = mainRepository.getWidgetByAppWidgetId(appWidgetId!!)
            }
            // Priority 3: Default behavior (app icon open) - load active/last widget
            else {
                couple = mainRepository.getActiveWidget()
            }
        }

        if (couple != null) {
            widgetId = couple!!.id

            yourName = couple!!.you?.name
            yourImage = couple!!.you?.image
            yourBirthday = couple!!.you?.birthday

            partnerName = couple!!.partner?.name
            partnerImage = couple!!.partner?.image
            partnerBirthday = couple!!.partner?.birthday

            // Stored resource IDs can go stale across builds — see DecoratorCatalog.
            shape = DecoratorCatalog.safeShape(couple!!.frame?.vector)
            shapeColor = couple!!.frame?.color

            symbol = DecoratorCatalog.safeSymbol(couple!!.heart?.vector)
            symbolColor = couple!!.heart?.color

            nameColor = couple!!.nameColor
            counterColor = couple!!.counterColor

            fallInLove = couple!!.fallInLove
            inRelation = couple!!.inRelation

            widgetBackground = couple!!.widgetBackground ?: 0
            widgetBackgroundLabel = widgetBackgroundTitles.getOrNull(widgetBackground ?: 0) ?: widgetBackgroundTitles[0]
            widgetBackgroundIcon = widgetBackgroundIcons.getOrNull(widgetBackground ?: 0) ?: widgetBackgroundIcons[0]
            widgetBackgroundColor = couple!!.widgetBackgroundColor ?: defaultBackgroundColor
            showBackgroundColor = (widgetBackground ?: 0) == solidBackgroundIndex
            fontStyle = couple!!.fontStyle ?: 0
            fontStyleLabel = fontStyleTitles.getOrNull(fontStyle ?: 0) ?: fontStyleTitles[0]
        } else {
            resetToDefaultData()
        }

        counterDate = dateDifference(inRelation, defaultDate)

    }

    private fun resetToDefaultData() {
        widgetId = null
        yourName = "You"
        yourImage = Uri.EMPTY
        yourBirthday = imuDate
        partnerName = "Partner"
        partnerImage = Uri.EMPTY
        partnerBirthday = ufmDate
        shape = R.drawable.shape_4
        shapeColor = Color.WHITE
        symbol = R.drawable.symbol_6
        symbolColor = Color.WHITE
        nameColor = Color.WHITE
        counterColor = Color.WHITE
        fallInLove = fallDate
        inRelation = relationDate
        widgetBackground = 0
        widgetBackgroundLabel = widgetBackgroundTitles[0]
        widgetBackgroundIcon = widgetBackgroundIcons[0]
        widgetBackgroundColor = defaultBackgroundColor
        showBackgroundColor = false
        fontStyle = 0
        fontStyleLabel = fontStyleTitles[0]
    }

    private fun hasWidgetOnHomeScreen(context: Context): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, CoupleWidgetProvider::class.java)
        )
        return widgetIds.isNotEmpty()
    }

    private fun guideUserToAddWidget(activity: Activity) {
        val appWidgetManager = AppWidgetManager.getInstance(activity)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
            appWidgetManager.isRequestPinAppWidgetSupported) {

            val widgetProvider = ComponentName(activity, CoupleWidgetProvider::class.java)

            // Pass null for successCallback — we do NOT re-launch the app after pinning.
            // Instead we minimize so the user sees the home screen and can position the widget.
            val pinned = appWidgetManager.requestPinAppWidget(widgetProvider, null, null)

            if (pinned) {
                // System pin dialog is now showing — minimize after a short delay
                // so when the user taps "Add", they land directly on the home screen
                toast = "Almost done! Tap 'Add' to place the widget on your home screen"
                Handler(Looper.getMainLooper()).postDelayed({
                    activity.moveTaskToBack(true)
                }, 800)
            } else {
                toast = "Widget saved! Add it to your home screen from the widget menu"
                Handler(Looper.getMainLooper()).postDelayed({
                    activity.moveTaskToBack(true)
                }, 1500)
            }
        } else {
            // Android < 8.0 or device doesn't support pin request
            toast = "Widget saved! Long-press your home screen to add the widget"
            Handler(Looper.getMainLooper()).postDelayed({
                activity.moveTaskToBack(true)
            }, 1500)
        }
    }

    fun getNextBackground(): Int {
        val backgrounds = listOf(
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5
        )

        val lastIndex = preference.sharedPref.getInt("last_bg_index", -1)
        val nextIndex = (lastIndex + 1) % backgrounds.size

        preference.sharedPref.edit { putInt("last_bg_index", nextIndex) }

        return backgrounds[nextIndex]
    }


}


