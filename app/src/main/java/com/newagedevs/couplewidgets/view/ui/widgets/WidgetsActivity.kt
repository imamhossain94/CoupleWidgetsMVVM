package com.newagedevs.couplewidgets.view.ui.widgets

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityWidgetsBinding
import com.newagedevs.couplewidgets.view.adapter.WidgetsAdapter
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.intentOf
import org.koin.android.viewmodel.ext.android.getViewModel


class WidgetsActivity : BindingActivity<ActivityWidgetsBinding>(R.layout.activity_widgets) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding {
            dispatcher = this@WidgetsActivity
            adapter = WidgetsAdapter(getPreviewFrame())
            vm = getViewModel()
        }

    }

    private fun getPreviewFrame():Drawable? {
        val wallpaperManager = WallpaperManager.getInstance(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        return wallpaperManager.drawable
    }

    companion object {
        fun startActivity(
            context: Context,
        ) = context.intentOf<WidgetsActivity> {
            context.startActivity(intent, null)
        }
    }
}

