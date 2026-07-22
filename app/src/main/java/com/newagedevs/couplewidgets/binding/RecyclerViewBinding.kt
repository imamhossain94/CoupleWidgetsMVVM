package com.newagedevs.couplewidgets.binding


import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.adapter.MemoriesAdapter
import com.newagedevs.couplewidgets.view.adapter.WidgetsAdapter
import com.newagedevs.couplewidgets.view.ui.memories.MemoryListItem
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

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: CoordinatorLayout, text: String?) {
        text.whatIfNotNullOrEmpty {
            Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Submits to [MemoriesAdapter]. Unlike the widgets binding this must accept an
     * empty list — deleting the last memory has to clear the timeline, not leave
     * the previous rows on screen.
     */
    @JvmStatic
    @BindingAdapter("adapterMemoryList")
    fun bindMemoryList(view: RecyclerView, items: List<MemoryListItem>?) {
        view.adapter.whatIfNotNullAs<MemoriesAdapter> { adapter ->
            adapter.submitList(items.orEmpty())
        }
    }




}
