package com.newagedevs.couplewidgets.view.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.maxkeppeler.sheets.option.Option
import com.maxkeppeler.sheets.option.OptionSheet
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.px
import com.newagedevs.couplewidgets.repository.MainRepository
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import timber.log.Timber


class MainViewModel constructor(
    private val mainRepository: MainRepository
) : BindingViewModel() {

    @get:Bindable
    var isLoading: Boolean by bindingProperty(false)
        private set

    @get:Bindable
    var toast: String? by bindingProperty(null)
        private set

    @get:Bindable
    var yourName: String? by bindingProperty(null)

    @get:Bindable
    var partnerName: String? by bindingProperty(null)

    @get:Bindable
    var yourImage: Bitmap? by bindingProperty(null)

    @get:Bindable
    var partnerImage: Bitmap? by bindingProperty(null)


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

            }
        }


    }

    fun colorPicker(view: View) {

        ColorSheet().show(view.context) {
            title("Select color")
            onPositive { color ->
                // Use color
                val hexColor = "#${Integer.toHexString(color).uppercase()}"

                val textView = view as TextView
                textView.text = hexColor
                textView.setTextColor(color)
                textView.compoundDrawables[0].setTint(color)

                textView.tag

            }
        }


    }

    // Couple details
    fun imagePicker(view: View) {

        val textView = view as TextView

        val requestCode = if (textView.tag == "Your image")  1094 else 1095

        OptionSheet().show(view.context) {
            title("Choose")
            with(
                Option(R.drawable.ic_camera, "Camera"),
                Option(R.drawable.ic_picture, "Gallery")
            )
            onPositive { index: Int, _: Option ->
                if(index == 0) {
                    ImagePicker.with(view.context as Activity)
                        .cameraOnly()
                        .cropSquare()
                        .start(requestCode)
                }else if(index == 1) {
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

        CalendarSheet().show(view.context) {
            title("What's your date of birth?")
            selectionMode(SelectionMode.DATE)
            onPositive { dateStart, _ ->
                // Handle date or range
                val textView = view as TextView
                textView.text = dateStart.toLocalDate().toString()
            }

        }

    }

    init {
        Timber.d("injection DashboardViewModel")
    }

}


