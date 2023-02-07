package com.newagedevs.couplewidgets.view.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.Bindable
import com.github.dhaval2404.imagepicker.ImagePicker
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.calendar.utils.toLocalDate
import com.maxkeppeler.sheets.color.ColorSheet
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.px
import com.newagedevs.couplewidgets.repository.MainRepository
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MainViewModel constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @get:Bindable
    var yourName: String? by bindingProperty("nickname")

    @get:Bindable
    var partnerName: String? by bindingProperty("nickname")

    @get:Bindable
    var yourImage: Bitmap? by bindingProperty(null)

    @get:Bindable
    var partnerImage: Bitmap? by bindingProperty(null)

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
    var fallInLove: String? by bindingProperty(dateFormat.format(Calendar.getInstance().time))

    @get:Bindable
    var inRelation: String? by bindingProperty(dateFormat.format(Calendar.getInstance().time))

    @get:Bindable
    var yourBirthday: String? by bindingProperty(dateFormat.format(Calendar.getInstance().time))

    @get:Bindable
    var partnerBirthday: String? by bindingProperty(dateFormat.format(Calendar.getInstance().time))

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
                // Use color
                val hexColor = "#${Integer.toHexString(color).uppercase()}"

                textView.text = hexColor
                textView.setTextColor(color)
                textView.compoundDrawables[0].setTint(color)

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
        val title = if (textView.tag == "Your image") "Choose your image" else "Choose your partner image"

        OptionSheet().show(view.context) {
            title(title)
            with(
                Option(R.drawable.ic_camera, "Camera"),
                Option(R.drawable.ic_picture, "Gallery")
            )
            onPositive { index: Int, _: Option ->
                if (index == 0) {
                    ImagePicker.with(view.context as Activity)
                        .cameraOnly()
                        .cropSquare()
                        .start(requestCode)
                } else if (index == 1) {
                    ImagePicker.with(view.context as Activity)
                        .galleryOnly()
                        .cropSquare()
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
        when (tag) {
            "Fall in Love" -> {
                title = "What's the date you fell in love?"
            }
            "In Relation" -> {
                title = "When did your relationship start?"
            }
            "Your Birthday" -> {
                title = "What's your date of birth?"
            }
            "Partner Birthday" -> {
                title = "What's your partner's birthday?"
            }
        }

        CalendarSheet().show(view.context) {
            title(title)
            selectionMode(SelectionMode.DATE)
            onPositive { dateStart, _ ->

                val date = dateStart.toLocalDate().toString()

                when (tag) {
                    "Fall in Love" -> {
                        fallInLove = date
                    }
                    "In Relation" -> {
                        inRelation = date
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
        }

    }


    init {
        Timber.d("injection DashboardViewModel")
        initializeData()
    }

}


