package com.newagedevs.couplewidgets.binding

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ViewBinding {
  @JvmStatic
  @BindingAdapter("app:resource")
  fun setImageResource(view: ImageView, resId: Int?) {
    Glide.with(view.context)
      .load(resId)
      .into(view)
  }

  @JvmStatic
  @BindingAdapter("app:bitmap")
  fun setImageBitmap(view: ImageView, bitmap: Bitmap?) {
    view.setImageBitmap(bitmap)
  }

}
