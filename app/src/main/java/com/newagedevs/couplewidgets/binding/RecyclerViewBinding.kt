package com.newagedevs.couplewidgets.binding


import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.adapter.WidgetsAdapter
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
    @BindingAdapter("adapterWidgetsList")
    fun bindWidgetsList(view: RecyclerView, horses: List<Couple>?) {
        horses.whatIfNotNullOrEmpty { items ->
            view.adapter.whatIfNotNullAs<WidgetsAdapter> { adapter ->
                adapter.updateWidgetsList(items)
            }
        }
    }




}
