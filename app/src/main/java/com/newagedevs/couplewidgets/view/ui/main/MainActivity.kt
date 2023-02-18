package com.newagedevs.couplewidgets.view.ui.main

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.ui.CustomSheet
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.bundle
import com.skydoves.bundler.intentOf
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val widgetID: Long? by bundle("widgetId")
    private val appWidgetIDs: IntArray? by bundle("appWidgetIds")
    private val viewModel: MainViewModel by viewModel { parametersOf(widgetID, appWidgetIDs) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding {
            vm = viewModel
        }

        setupPreviewFrame()
    }

    private fun setupPreviewFrame() {
        val preview = findViewById<ImageView>(R.id.bg)
        val wallpaperManager = WallpaperManager.getInstance(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                3
            )
            return
        }
        val wallpaperDrawable = wallpaperManager.drawable
        preview.setImageDrawable(wallpaperDrawable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setupPreviewFrame()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {

                val uri: Uri = data?.data!!

                when (requestCode) {
                    1094 -> {
                        viewModel.yourImage = uri
                    }
                    1095 -> {
                        viewModel.partnerImage = uri
                    }
                }
            }
            ImagePicker.RESULT_ERROR -> {
                viewModel.toast = ImagePicker.getError(data)
            }
            else -> {
                viewModel.toast = "Task Cancelled"
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        CustomSheet().show(this@MainActivity) {
            style(SheetStyle.BOTTOM_SHEET)
            title("Confirm Exit")
            content("Are you sure you want to exit? Hope you will come back again.")
            onPositive("Exit") {
                finish()
            }
        }
    }


    companion object {
        fun startActivity(
            context: Context,
            couple: Couple
        ) = context.intentOf<MainActivity> {
            putExtra("widgetId", couple.id.toInt())
            context.startActivity(intent, null)
            (context as Activity).finish()
        }

        fun restartActivity(
            context: Context
        ) = context.intentOf<MainActivity> {
            context.startActivity(intent, null)
            (context as Activity).finish()
        }
    }

}