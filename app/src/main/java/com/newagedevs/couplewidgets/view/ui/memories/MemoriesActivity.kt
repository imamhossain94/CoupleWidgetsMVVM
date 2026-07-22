package com.newagedevs.couplewidgets.view.ui.memories

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ActivityMemoriesBinding
import com.newagedevs.couplewidgets.view.adapter.MemoriesAdapter
import com.newagedevs.couplewidgets.view.adapter.SwipeToDeleteCallback
import com.skydoves.bindables.BindingActivity
import com.skydoves.bundler.intentOf
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MemoriesActivity : BindingActivity<ActivityMemoriesBinding>(R.layout.activity_memories) {

    private lateinit var viewModel: MemoriesViewModel
    private lateinit var memoriesAdapter: MemoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = getViewModel()
        memoriesAdapter = MemoriesAdapter { memory ->
            MemoryEditorActivity.startActivity(this, memory.id)
        }

        binding {
            dispatcher = this@MemoriesActivity
            adapter = memoriesAdapter
            vm = viewModel
        }

        binding.addMemory.apply {
            // ExtendedFAB has no gradient background of its own, so paint one on.
            background = gradientPill()
            setOnClickListener { MemoryEditorActivity.startActivity(this@MemoriesActivity, null) }
        }

        attachSwipeToDelete()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        // Picks up memories added in the editor and any change to the
        // relationship date on the main screen.
        viewModel.refresh()
    }

    private fun gradientPill(): GradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            ContextCompat.getColor(this, R.color.love_rose),
            ContextCompat.getColor(this, R.color.love_orchid)
        )
    ).apply { cornerRadius = resources.getDimension(R.dimen.radius_l) }

    /**
     * Swipe a saved memory away, with an undo. Milestones and headers aren't
     * swipeable — they're computed, so there is nothing to delete.
     */
    private fun attachSwipeToDelete() {
        val callback = SwipeToDeleteCallback(
            context = this,
            canSwipe = { position -> memoriesAdapter.memoryAt(position) != null },
            onDelete = { position ->
                val memory = memoriesAdapter.memoryAt(position) ?: return@SwipeToDeleteCallback
                viewModel.delete(memory)

                Snackbar.make(binding.main, R.string.memory_deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { viewModel.restore(memory) }
                    .setActionTextColor(
                        ContextCompat.getColor(this, R.color.love_rose_light)
                    )
                    .setAnchorView(binding.addMemory)
                    .show()
            }
        )

        ItemTouchHelper(callback).attachToRecyclerView(binding.memoryList)
    }

    companion object {
        fun startActivity(context: Context) =
            context.intentOf<MemoriesActivity> {
                context.startActivity(intent, null)
            }
    }
}
