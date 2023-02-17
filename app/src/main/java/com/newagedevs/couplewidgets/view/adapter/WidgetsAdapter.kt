package com.newagedevs.couplewidgets.view.adapter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ItemWidgetBinding
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.model.Couple
import com.skydoves.bindables.binding
import java.text.SimpleDateFormat
import java.util.*

class WidgetsAdapter(
    background: Drawable?
) : RecyclerView.Adapter<WidgetsAdapter.WidgetsViewHolder>() {

    private val items = mutableListOf<Couple>()
    private val defaultDate =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetsViewHolder {

        val binding = parent.binding<ItemWidgetBinding>(R.layout.item_widget)

        return WidgetsViewHolder(binding).apply {

            binding.root.setOnClickListener { view ->
                val position =
                    adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return@setOnClickListener

//        MainActivity.startActivity(
//          view.context,
//          items[position]
//        )
            }
        }

    }

    fun updateWidgetsList(widgets: List<Couple>) {
        items.clear()
        items.addAll(widgets)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WidgetsViewHolder, position: Int) {
        holder.binding.apply {
            couple = items[position]
            counter = dateDifference(items[position].inRelation, defaultDate)
            background = background
            executePendingBindings()
        }
    }

    fun getCouple(index: Int): Couple = items[index]

    class WidgetsViewHolder(val binding: ItemWidgetBinding) :
        RecyclerView.ViewHolder(binding.root)

}

