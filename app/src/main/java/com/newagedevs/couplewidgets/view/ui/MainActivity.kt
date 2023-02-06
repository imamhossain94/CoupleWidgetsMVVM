package com.newagedevs.couplewidgets.view.ui

import android.os.Bundle
import android.widget.EditText
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMainBinding
import com.newagedevs.couplewidgets.view.adapter.HeartSymbolAdapter
import com.newagedevs.couplewidgets.view.adapter.ImageShapeAdapter
import com.skydoves.bindables.BindingActivity
import org.koin.android.viewmodel.ext.android.getViewModel


class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding {
            vm = getViewModel()
            shape = ImageShapeAdapter()
            symbol = HeartSymbolAdapter()
        }

        val xview:EditText = this.findViewById(R.id.et_your_name)

        xview.setText("Imam Hossain")


    }


}