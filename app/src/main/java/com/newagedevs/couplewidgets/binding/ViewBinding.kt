package com.newagedevs.couplewidgets.binding

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker

object ViewBinding {
  @JvmStatic
  @BindingAdapter(value = ["app:resource", "app:tint"], requireAll = false)
  fun setImageResource(view: ImageView, resource: Int?, tint: Int? ) {
    Glide.with(view.context)
      .load(resource)
      .into(view)
    view.setColorFilter(tint!!)
  }

  @JvmStatic
  @BindingAdapter(value = ["app:bitmap", "app:shape"], requireAll = false)
  fun setImageBitmap(view: ImageView, bitmap: Bitmap?, shape:Int) {

    view.setImageBitmap(bitmap)

    if(bitmap != null){
      val newBitmap = VectorDrawableMasker.maskImage(view.context, bitmap, shape, view.width, 5)
      view.setImageBitmap(newBitmap)
    }

  }


}
