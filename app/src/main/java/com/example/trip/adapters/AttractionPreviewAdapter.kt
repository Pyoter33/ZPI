package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trip.databinding.ItemAttractionPreviewBinding
import com.example.trip.models.AttractionPreview
import com.skydoves.balloon.Balloon
import javax.inject.Inject


class AttractionPreviewAdapter @Inject constructor() :
    ListAdapter<AttractionPreview, AttractionPreviewAdapter.AttractionPreviewViewHolder>(AttractionPreviewDiffUtil()) {


    private lateinit var attractionPreviewClickListener: AttractionPreviewClickListener

    private lateinit var popupMenu: Balloon

    fun setAttractionClickListener(attractionPreviewClickListener: AttractionPreviewClickListener) {
        this.attractionPreviewClickListener = attractionPreviewClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionPreviewViewHolder {
        return AttractionPreviewViewHolder.create(parent, attractionPreviewClickListener)
    }

    override fun onBindViewHolder(holder: AttractionPreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttractionPreviewViewHolder(
        private val binding: ItemAttractionPreviewBinding,
        private val attractionClickListener: AttractionPreviewClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: AttractionPreview) {
            with(binding) {
                textName.text = attraction.name
                textAddress.text = attraction.address
                Glide.with(itemView).load(attraction.imageUrl).centerCrop().into(binding.imageAccommodation)

            }
            onClick(attraction)
            onSeeMoreClick(attraction)
        }

        private fun onClick(attraction: AttractionPreview) {
            binding.card.setOnClickListener {
                attractionClickListener.onClick(attraction)
            }
        }

        private fun onSeeMoreClick(attraction: AttractionPreview) {
            binding.buttonSeeInMaps.setOnClickListener {
                attractionClickListener.onSeeMoreClick(attraction)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                attractionPreviewClickListener: AttractionPreviewClickListener
            ): AttractionPreviewViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionPreviewBinding.inflate(layoutInflater, parent, false)
                return AttractionPreviewViewHolder(
                    binding,
                    attractionPreviewClickListener,
                )
            }
        }
    }
}

class AttractionPreviewDiffUtil : DiffUtil.ItemCallback<AttractionPreview>() {
    override fun areItemsTheSame(oldItem: AttractionPreview, newItem: AttractionPreview): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: AttractionPreview, newItem: AttractionPreview): Boolean {
        return oldItem.id == newItem.id
    }
}

interface AttractionPreviewClickListener {
    fun onClick(attractionPreview: AttractionPreview)
    fun onSeeMoreClick(attraction: AttractionPreview)
}