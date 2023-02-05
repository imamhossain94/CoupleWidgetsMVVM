package com.newagedevs.couplewidgets.binding


import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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

//    @JvmStatic
//    @BindingAdapter("adapterLocationList")
//    fun bindAdapterLocationList(view: RecyclerView, locations: List<Location>?) {
//        locations.whatIfNotNullOrEmpty { items ->
//            view.adapter.whatIfNotNullAs<LocationAdapter> { adapter ->
//                adapter.updateLocationList(items)
//            }
//        }
//    }


}
