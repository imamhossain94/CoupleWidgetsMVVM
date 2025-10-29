package com.newagedevs.couplewidgets.view.adapter

import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ItemWidgetBinding
import com.newagedevs.couplewidgets.extensions.dateDifference
import com.newagedevs.couplewidgets.model.Couple
import com.newagedevs.couplewidgets.view.ui.main.MainActivity
import com.skydoves.bindables.binding
import java.text.SimpleDateFormat
import java.util.*

class WidgetsAdapter() : RecyclerView.Adapter<WidgetsAdapter.WidgetsViewHolder>() {

    private val items = mutableListOf<Couple>()
    private val defaultDate =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)

    // Background drawables list
    private val backgrounds = listOf(
        R.drawable.bg_1,
        R.drawable.bg_2,
        R.drawable.bg_3,
        R.drawable.bg_4,
        R.drawable.bg_5
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetsViewHolder {

        val binding = parent.binding<ItemWidgetBinding>(R.layout.item_widget)

        return WidgetsViewHolder(binding).apply {

            binding.root.setOnClickListener { view ->
                val position =
                    bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return@setOnClickListener

                MainActivity.startActivity(
                    view.context,
                    items[position]
                )
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

            // Set background based on position (cycles through backgrounds)
            val bgDrawable = AppCompatResources.getDrawable(
                holder.itemView.context,
                backgrounds[position % backgrounds.size]
            )
            background = bgDrawable

            executePendingBindings()
        }
    }

    class WidgetsViewHolder(val binding: ItemWidgetBinding) :
        RecyclerView.ViewHolder(binding.root)

}