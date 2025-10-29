package com.newagedevs.couplewidgets.view.ui.widgets

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityWidgetsBinding
import com.newagedevs.couplewidgets.view.adapter.WidgetsAdapter
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.intentOf
import org.koin.android.viewmodel.ext.android.getViewModel

class WidgetsActivity : BindingActivity<ActivityWidgetsBinding>(R.layout.activity_widgets) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding {
            dispatcher = this@WidgetsActivity
            adapter = WidgetsAdapter()
            vm = getViewModel()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun startActivity(context: Context) =
            context.intentOf<WidgetsActivity> {
                context.startActivity(intent, null)
            }
    }
}
