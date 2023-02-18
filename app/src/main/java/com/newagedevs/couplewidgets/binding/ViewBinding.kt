package com.newagedevs.couplewidgets.binding

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.extensions.isUriEmpty
import com.newagedevs.couplewidgets.extensions.px
import com.newagedevs.couplewidgets.utils.VectorDrawableMasker
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import timber.log.Timber

object ViewBinding {

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: LinearLayout, text: String?) {
        text.whatIfNotNullOrEmpty {
            Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["app:drawableStart"], requireAll = false)
    fun drawableStartCompat(view: TextView, resource: Int?) {

        val image = ResourcesCompat.getDrawable(view.resources, resource!!, null)
        image?.setBounds(0, 0, 24.px, 24.px)

        view.setCompoundDrawables(image, null, null, null)

    }


    @JvmStatic
    @BindingAdapter(value = ["app:drawableBackground"], requireAll = false)
    fun drawableBackground(view: ImageView, resource: Drawable?) {
        view.setImageDrawable(resource)
    }


    @JvmStatic
    @BindingAdapter(value = ["app:resource", "app:tint"], requireAll = false)
    fun setImageResource(view: ImageView, resource: Int?, tint: Int?) {
        Glide.with(view.context)
            .load(resource)
            .into(view)
        view.setColorFilter(tint!!)
    }

    @JvmStatic
    @BindingAdapter(value = ["app:uri", "app:shape", "app:borderColor"], requireAll = false)
    fun setImageBitmap(view: ImageView, uri: Uri?, shape: Int, borderColor: Int) {

        Glide.with(view.context)
            .asBitmap()
            .load(uri)
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
                        5,
                        borderColor
                    )
                    view.setImageBitmap(newBitmap)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)

                    val newBitmap = VectorDrawableMasker.maskImage(
                        view.context,
                        null,
                        shape,
                        view.width,
                        5,
                        borderColor
                    )
                    view.setImageBitmap(newBitmap)
                }
            })

    }


}
