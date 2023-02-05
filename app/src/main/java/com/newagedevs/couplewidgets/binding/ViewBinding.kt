package com.newagedevs.couplewidgets.binding

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ViewBinding {
  @JvmStatic
  @BindingAdapter("loadImage")
  fun bindLoadImage(view: AppCompatImageView, url: String?) {
    Glide.with(view.context)
      .load(url)
      .into(view)
  }

}
