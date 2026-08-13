package com.newagedevs.couplewidgets.view.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R

/**
 * Swipe a row away in either direction, revealing a rose panel underneath.
 *
 * [canSwipe] lets a list mix swipeable and fixed rows — the memories timeline
 * has computed milestones and headers that must stay put.
 */
class SwipeToDeleteCallback(
    context: Context,
    private val canSwipe: (position: Int) -> Boolean,
    private val onDelete: (position: Int) -> Unit,
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.love_rose_deep)
    }
    private val radius = context.resources.getDimension(R.dimen.radius_l)
    private val inset = context.resources.getDimension(R.dimen.space_l)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ) = false

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION || !canSwipe(position)) return 0
        return super.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) onDelete(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        drawBackground(c, viewHolder.itemView, dX)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawBackground(canvas: Canvas, itemView: View, dX: Float) {
        if (dX == 0f) return

        // Inset to line up with the card margins, so the panel reads as sitting
        // behind the card rather than behind the whole row.
        val top = itemView.top.toFloat()
        val bottom = itemView.bottom.toFloat()
        val rect = if (dX > 0) {
            RectF(itemView.left + inset, top, itemView.left + dX, bottom)
        } else {
            RectF(itemView.right + dX, top, itemView.right - inset, bottom)
        }
        if (rect.right <= rect.left) return

        canvas.drawRoundRect(rect, radius, radius, paint)
    }
}
