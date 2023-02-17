package com.newagedevs.couplewidgets.view.ui.main

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.Bindable
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
import com.newagedevs.couplewidgets.repository.MainRepository
import com.newagedevs.couplewidgets.utils.Constants
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.newagedevs.couplewidgets.view.ui.widgets.WidgetsActivity
import com.newagedevs.couplewidgets.widgets.CoupleWidgetProvider
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    private val defaultDate =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

    @get:Bindable
    var toast: String? by bindingProperty(null)

    @get:Bindable
    var yourName: String? by bindingProperty("nickname")

    @get:Bindable
    var partnerName: String? by bindingProperty("nickname")

    @get:Bindable
    var yourImage: Uri? by bindingProperty(Uri.EMPTY)

    @get:Bindable
    var partnerImage: Uri? by bindingProperty(Uri.EMPTY)

    @get:Bindable
    var shape: Int? by bindingProperty(R.drawable.shape_1)

    @get:Bindable
    var shapeColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var symbol: Int? by bindingProperty(R.drawable.symbol_1)

    @get:Bindable
    var symbolColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var nameColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var counterColor: Int? by bindingProperty(Color.WHITE)

    @get:Bindable
    var fallInLove: String? by bindingProperty(defaultDate)

    @get:Bindable
    var inRelation: String? by bindingProperty(defaultDate)

    @get:Bindable
    var yourBirthday: String? by bindingProperty(defaultDate)

    @get:Bindable
    var partnerBirthday: String? by bindingProperty(defaultDate)

    @get:Bindable
    var counterDate: String? by bindingProperty(defaultDate)

    // Widget settings
    fun shapePicker(view: View) {
        val shapes = listOf(
            R.drawable.shape_1, R.drawable.shape_2, R.drawable.shape_3,
            R.drawable.shape_4, R.drawable.shape_5, R.drawable.shape_6
        )
        val shapeTitles = listOf(
            "Circle", "Tag", "Hexagon", "Square", "Heart", "Rounded"
        )

        OptionSheet().show(view.context) {
            title("Select image shape")
            with(
                Option(shapes[0], shapeTitles[0]),
                Option(shapes[1], shapeTitles[1]),
                Option(shapes[2], shapeTitles[2]),
                Option(shapes[3], shapeTitles[3]),
                Option(shapes[4], shapeTitles[4]),
                Option(shapes[5], shapeTitles[5]),
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
        val symbols = listOf(
            R.drawable.symbol_1, R.drawable.symbol_2, R.drawable.symbol_3, R.drawable.symbol_4,
            R.drawable.symbol_5, R.drawable.symbol_6, R.drawable.symbol_7, R.drawable.symbol_8
        )
        val symbolTitles = listOf(
            "Heart", "Broken", "Battery", "Heart", "Signal", "Bottle", "Heart", "Like"
        )

        OptionSheet().show(view.context) {
            title("Select heart symbol")
            with(
                Option(symbols[0], symbolTitles[0]),
                Option(symbols[1], symbolTitles[1]),
                Option(symbols[2], symbolTitles[2]),
                Option(symbols[3], symbolTitles[3]),
                Option(symbols[4], symbolTitles[4]),
                Option(symbols[5], symbolTitles[5]),
                Option(symbols[6], symbolTitles[6]),
                Option(symbols[7], symbolTitles[7]),
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
                val file = wrapper.getDir("images", Context.MODE_PRIVATE)

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

    fun openMenu(view: View) {

        val appVersion = getApplicationVersion()

        OptionSheet().show(view.context) {
            title("Menu")
            columns(3)
            displayMode(DisplayMode.GRID_VERTICAL)
            with(
                Option(R.drawable.ic_widgets, "Widgets"),
                Option(R.drawable.ic_share, "Share"),
                Option(R.drawable.ic_edit, "Write us"),
                Option(R.drawable.ic_feedback, "Feedback"),
                Option(R.drawable.ic_bug, "Bug reports"),
                Option(R.drawable.ic_privacy, "Privacy policy"),
                Option(R.drawable.ic_playstore, "Other apps"),
                Option(R.drawable.ic_star, "Rate us"),
                Option(R.drawable.ic_github, "Source code"),
                Option(R.drawable.ic_svg, "Icons by"),
                Option(R.drawable.ic_plugin, "V:$appVersion"),
                Option(R.drawable.ic_power, "Exit"),
            )
            onPositive { index: Int, _: Option ->
                when (index) {
                    0 -> {
                        WidgetsActivity.startActivity(view.context)
                    }
                    1 -> {
                        shareTheApp(requireActivity())
                    }
                    2 -> {
                        openMailApp(requireActivity(), "Writing about app", Constants.contactMail)
                    }
                    3 -> {
                        openMailApp(requireActivity(), "Feedback", Constants.feedbackMail)
                    }
                    4 -> {
                        openMailApp(requireActivity(), "Bug reports", Constants.feedbackMail)
                    }
                    5 -> {
                        openWebPage(requireActivity(), Constants.privacyPolicyUrl) { toast = it }
                    }
                    6 -> {
                        openAppStore(requireActivity(), Constants.publisherName) { toast = it }
                    }
                    7 -> {
                        openAppStore(requireActivity(), Constants.appStoreId) { toast = it }
                    }
                    8 -> {
                        openWebPage(requireActivity(), Constants.sourceCodeUrl) { toast = it }
                    }
                    9 -> {
                        toast = "Icons by svgrepo.com"
                    }
                    10 -> {
                        toast = "Version: $appVersion"
                    }
                    11 -> {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    fun submitData(view: View) {

        val couple = Couple(
            id = 0,
            frame = Decorator(shape, shapeColor),
            heart = Decorator(symbol, symbolColor),
            nameColor = nameColor,
            counterColor = counterColor,
            you = Person(yourName, yourBirthday, yourImage),
            partner = Person(partnerName, partnerBirthday, partnerImage),
            fallInLove = fallInLove,
            inRelation = inRelation
        )

        CustomSheet().show(view.context) {
            style(SheetStyle.BOTTOM_SHEET)
            title("Confirm Changes")
            content("Do you want to save the new changes?")
            onPositive("Yes") {
                toast = "Changes saved successfully"
                mainRepository.setCouple(couple)
                // Update widget
                val context = view.context
                val activity = view.context as Activity

                val intent = Intent(context, CoupleWidgetProvider::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                val id = activity.intent.extras?.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                val idsExtra = activity.intent.extras?.getIntArray("ids")

                if (idsExtra != null) {
                    context.sendBroadcast(intent)
                    activity.finish()
                } else if (id != null) {
                    activity.setResult(RESULT_OK, intent)
                    activity.finish()
                } else {
                    context.sendBroadcast(intent)
                }

            }
            onNegative("No") {
                toast = "Changes not saved"
            }
        }

    }

    private fun initializeData() {

        val couple = mainRepository.getCouple()

        if (couple != null) {
            yourName = couple.you?.name
            yourImage = couple.you?.image
            yourBirthday = couple.you?.birthday

            partnerName = couple.partner?.name
            partnerImage = couple.partner?.image
            partnerBirthday = couple.partner?.birthday

            shape = couple.frame?.vector
            shapeColor = couple.frame?.color

            symbol = couple.heart?.vector
            symbolColor = couple.heart?.color

            nameColor = couple.nameColor
            counterColor = couple.counterColor

            fallInLove = couple.fallInLove
            inRelation = couple.inRelation
        }

        counterDate = dateDifference(inRelation, defaultDate)


    }


    init {
        Timber.d("injection DashboardViewModel")
        initializeData()
    }

}


