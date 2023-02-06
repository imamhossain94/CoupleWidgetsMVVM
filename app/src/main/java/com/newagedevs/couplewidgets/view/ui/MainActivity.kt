package com.newagedevs.couplewidgets.view.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.newagedevs.couplewidgets.model.HeartSymbol
import com.newagedevs.couplewidgets.model.ImageShape
import com.newagedevs.couplewidgets.view.adapter.HeartSymbolAdapter
import com.newagedevs.couplewidgets.view.adapter.ImageShapeAdapter
import com.skydoves.bindables.BindingActivity
import org.koin.android.viewmodel.ext.android.getViewModel


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel()

        binding {
            vm = viewModel
            shape = ImageShapeAdapter()
            symbol = HeartSymbolAdapter()
        }

        val shapes = ArrayList<ImageShape>()
        val symbols = ArrayList<HeartSymbol>()

        for (i in 1..8) {
            if (i <= 6) {
                shapes.add(
                    ImageShape(
                        resID = resources.getIdentifier("shape_$i", "drawable", packageName),
                        active = false
                    )
                )
            }
            symbols.add(
                HeartSymbol(
                    resID = resources.getIdentifier("symbol_$i", "drawable", packageName),
                    active = false
                )
            )
        }

        viewModel.imageShapeList = shapes
        viewModel.heartSymbolList = symbols

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


}