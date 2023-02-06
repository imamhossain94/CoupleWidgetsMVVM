package com.newagedevs.couplewidgets.binding

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ViewBinding {
  @JvmStatic
  @BindingAdapter("imageSrc")
  fun bindLoadImage(view: ImageView, resId: Int?) {
    Glide.with(view.context)
      .load(resId)
      .into(view)
  }

}
