package com.newagedevs.couplewidgets.binding


import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.model.HeartSymbol
import com.newagedevs.couplewidgets.model.ImageShape
import com.newagedevs.couplewidgets.view.adapter.HeartSymbolAdapter
import com.newagedevs.couplewidgets.view.adapter.ImageShapeAdapter
import com.skydoves.whatif.whatIfNotNullAs
import com.skydoves.whatif.whatIfNotNullOrEmpty

object RecyclerViewBinding {
    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: RecyclerView, baseAdapter: RecyclerView.Adapter<*>) {
        view.adapter = baseAdapter
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: ConstraintLayout, text: String?) {
        text.whatIfNotNullOrEmpty {
            Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
        }
    }

    @JvmStatic
    @BindingAdapter("adapterImageShapeList")
    fun bindAdapterImageShapeList(view: RecyclerView, shapes: List<ImageShape>?) {
        shapes.whatIfNotNullOrEmpty { items ->
            view.adapter.whatIfNotNullAs<ImageShapeAdapter> { adapter ->
                adapter.updateImageShapeList(items)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("adapterHeartSymbolList")
    fun bindAdapterHeartSymbolList(view: RecyclerView, symbols: List<HeartSymbol>?) {
        symbols.whatIfNotNullOrEmpty { items ->
            view.adapter.whatIfNotNullAs<HeartSymbolAdapter> { adapter ->
                adapter.updateHeartSymbolList(items)
            }
        }
    }


}
