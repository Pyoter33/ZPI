package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemTextBinding


class TextAdapter constructor() :
    ListAdapter<String, TextAdapter.TextViewHolder>(StringDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TextViewHolder(
        private val binding: ItemTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String) {
            binding.text.text = text
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTextBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(
                    binding
                )
            }
        }

    }
}

class StringDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}