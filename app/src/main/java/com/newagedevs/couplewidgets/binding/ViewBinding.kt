package com.newagedevs.couplewidgets.binding

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker

object ViewBinding {
    @JvmStatic
    @BindingAdapter(value = ["app:resource", "app:tint"], requireAll = false)
    fun setImageResource(view: ImageView, resource: Int?, tint: Int?) {
        Glide.with(view.context)
            .load(resource)
            .into(view)
        view.setColorFilter(tint!!)
    }

    @JvmStatic
    @BindingAdapter(value = ["app:bitmap", "app:shape"], requireAll = false)
    fun setImageBitmap(view: ImageView, bitmap: Bitmap?, shape: Int) {

        if (bitmap != null) {
            val newBitmap =
                VectorDrawableMasker.maskImage(view.context, bitmap, shape, view.width, 5)
            view.setImageBitmap(newBitmap)
        } else {

            Glide.with(view.context)
                .asBitmap()
                .load(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val newBitmap = VectorDrawableMasker.maskImage(
                            view.context,
                            resource,
                            shape,
                            view.width,
                            5
                        )
                        view.setImageBitmap(newBitmap)
                    }
                })

        }

    }


}
