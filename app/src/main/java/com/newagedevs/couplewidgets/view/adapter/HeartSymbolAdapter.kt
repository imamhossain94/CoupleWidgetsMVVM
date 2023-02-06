package com.newagedevs.couplewidgets.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ItemSymbolBinding
import com.newagedevs.couplewidgets.model.HeartSymbol
import com.skydoves.bindables.binding


class HeartSymbolAdapter : RecyclerView.Adapter<HeartSymbolAdapter.HeartSymbolViewHolder>() {

    private val items = mutableListOf<HeartSymbol>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeartSymbolViewHolder {

        val binding = parent.binding<ItemSymbolBinding>(R.layout.item_symbol)

        return HeartSymbolViewHolder(binding).apply {
            binding.root.setOnClickListener { view ->
                val position =
                    adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return@setOnClickListener
                // Update UI position
            }
        }

    }

    fun updateHeartSymbolList(symbols: List<HeartSymbol>) {
        items.clear()
        items.addAll(symbols)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HeartSymbolViewHolder, position: Int) {
        holder.binding.apply {
            symbol = items[position]
            executePendingBindings()
        }
    }

    fun getImageShape(index: Int): HeartSymbol = items[index]

    class HeartSymbolViewHolder(val binding: ItemSymbolBinding) :
        RecyclerView.ViewHolder(binding.root)
}

