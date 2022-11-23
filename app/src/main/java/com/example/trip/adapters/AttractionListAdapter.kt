package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trip.R
import com.example.trip.databinding.ItemAttractionAddMoreBinding
import com.example.trip.databinding.ItemAttractionBinding
import com.example.trip.models.Attraction
import com.example.trip.utils.setGone
import com.example.trip.utils.setInvisible
import com.example.trip.utils.setVisible
import javax.inject.Inject


class AttractionListAdapter @Inject constructor() :
    ListAdapter<Attraction, RecyclerView.ViewHolder>(AttractionDiffUtil()) {

    private lateinit var attractionClickListener: AttractionClickListener

    fun setAttractionClickListener(attractionClickListener: AttractionClickListener) {
        this.attractionClickListener = attractionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttractionViewHolder.create(parent, attractionClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AttractionViewHolder -> holder.bind(getItem(position))
            is AddMoreViewHolder -> holder.bind()
        }
    }

    class AttractionViewHolder(
        private val binding: ItemAttractionBinding,
        private val attractionClickListener: AttractionClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: Attraction) {
            with(binding) {
                textName.text = attraction.name
                textAddress.text = attraction.address
                textDescription.text = attraction.description

                if (attraction.distanceToNext != null) {
                    textDistance.text =
                        itemView.resources.getString(R.string.format_km, attraction.distanceToNext)
                    imageLine.setVisible()
                } else {
                    textDistance.text = ""
                    imageLine.setInvisible()
                }

                Glide.with(itemView).load(attraction.imageUrl).centerCrop()
                    .into(binding.imageAttraction)

                if (attraction.description.isNullOrEmpty()) {
                    buttonExpand.setInvisible()
                } else {
                    buttonExpand.setVisible()
                }

                if (attraction.isExpanded) {
                    buttonExpand.isSelected = true
                    extraLayout.setVisible()
                } else {
                    buttonExpand.isSelected = false
                    extraLayout.setGone()
                }
            }
            setOnSeeMoreClick(attraction.link)
            setOnExpandClick()
            setOnLongClick(attraction)
        }

        private fun setOnSeeMoreClick(link: String) {
            binding.buttonSeeInMaps.setOnClickListener {
                attractionClickListener.onSeeMoreClick(link)
            }
        }

        private fun setOnExpandClick() {
            binding.buttonExpand.setOnClickListener {
                attractionClickListener.onExpandClick(bindingAdapterPosition)
            }
        }

        private fun setOnLongClick(attraction: Attraction) {
            binding.card.setOnLongClickListener {
                attractionClickListener.onLongClick(attraction, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                attractionClickListener: AttractionClickListener
            ): AttractionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionBinding.inflate(layoutInflater, parent, false)
                return AttractionViewHolder(
                    binding,
                    attractionClickListener
                )
            }
        }
    }

    class AddMoreViewHolder(
        private val binding: ItemAttractionAddMoreBinding,
        private val attractionClickListener: AttractionClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.addMoreFab.setOnClickListener {
                attractionClickListener.onAddMoreClick()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                attractionClickListener: AttractionClickListener
            ): AddMoreViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionAddMoreBinding.inflate(layoutInflater, parent, false)
                return AddMoreViewHolder(
                    binding,
                    attractionClickListener,
                )
            }
        }
    }
}

class AttractionDiffUtil : DiffUtil.ItemCallback<Attraction>() {
    override fun areItemsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Attraction, newItem: Attraction): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface AttractionClickListener {
    fun onExpandClick(position: Int)
    fun onSeeMoreClick(link: String)
    fun onLongClick(attraction: Attraction, view: View)
    fun onAddMoreClick()
}