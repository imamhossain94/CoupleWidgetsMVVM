package com.newagedevs.couplewidgets.view.ui.widgets

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityWidgetsBinding
import com.newagedevs.couplewidgets.view.adapter.SwipeToDeleteCallback
import com.newagedevs.couplewidgets.view.adapter.WidgetsAdapter
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.intentOf
import org.koin.androidx.viewmodel.ext.android.getViewModel

class WidgetsActivity : BindingActivity<ActivityWidgetsBinding>(R.layout.activity_widgets) {

    private lateinit var viewModel: WidgetsViewModel
    private lateinit var widgetsAdapter: WidgetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = getViewModel()
        widgetsAdapter = WidgetsAdapter()

        binding {
            dispatcher = this@WidgetsActivity
            adapter = widgetsAdapter
            vm = viewModel
        }

        attachSwipeToDelete()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /** Swipe a widget card away, with an undo that restores it id and all. */
    private fun attachSwipeToDelete() {
        val callback = SwipeToDeleteCallback(
            context = this,
            canSwipe = { position -> widgetsAdapter.itemAt(position) != null },
            onDelete = { position ->
                val couple = widgetsAdapter.itemAt(position) ?: return@SwipeToDeleteCallback
                viewModel.deleteWidget(couple)

                Snackbar.make(binding.main, R.string.widget_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.restoreWidget(couple) }
                    .setActionTextColor(
                        ContextCompat.getColor(this, R.color.love_rose_light)
                    )
                    .show()
            }
        )

        ItemTouchHelper(callback).attachToRecyclerView(binding.widgetList)
    }

    companion object {
        fun startActivity(context: Context) =
            context.intentOf<WidgetsActivity> {
                context.startActivity(intent, null)
            }
    }
}
