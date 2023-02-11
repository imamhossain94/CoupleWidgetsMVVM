package com.newagedevs.couplewidgets.view.ui

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.maxkeppeler.sheets.core.SheetStyle
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.skydoves.bindables.BindingActivity
import org.koin.android.viewmodel.ext.android.getViewModel
import java.io.InputStream


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel()

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


//                var bitmap: Bitmap
//                var inputStream: InputStream? = null
//                try {
//                    inputStream = this.contentResolver.openInputStream(uri)
//                    inputStream?.let {
//                        bitmap = BitmapFactory.decodeStream(it)
//
//                        when (requestCode) {
//                            1094 -> {
//                                viewModel.yourImage = bitmap
//                            }
//                            1095 -> {
//                                viewModel.partnerImage = bitmap
//                            }
//                        }
//                    }
//                } finally {
//                    inputStream?.close()
//                }
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

}