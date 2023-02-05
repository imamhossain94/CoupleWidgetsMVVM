package com.newagedevs.couplewidgets.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newagedevs.couplewidgets.R
import com.newagedevs.couplewidgets.databinding.ItemShapeBinding
import com.newagedevs.couplewidgets.model.ImageShape
import com.skydoves.bindables.binding


class ImageShapeAdapter : RecyclerView.Adapter<ImageShapeAdapter.ImageShapeViewHolder>() {

    private val items = mutableListOf<ImageShape>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageShapeViewHolder {

        val binding = parent.binding<ItemShapeBinding>(R.layout.item_shape)

        return ImageShapeViewHolder(binding).apply {
            binding.root.setOnClickListener { view ->
                val position =
                    adapterPosition.takeIf { it != RecyclerView.NO_POSITION }
                        ?: return@setOnClickListener
                // Update UI position
            }
        }

    }

    fun updateImageShapeList(shapes: List<ImageShape>) {
        items.clear()
        items.addAll(shapes)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ImageShapeViewHolder, position: Int) {
        holder.binding.apply {
            shape = items[position]
            executePendingBindings()
        }
    }

    fun getImageShape(index: Int): ImageShape = items[index]

    class ImageShapeViewHolder(val binding: ItemShapeBinding) :
        RecyclerView.ViewHolder(binding.root)
}

