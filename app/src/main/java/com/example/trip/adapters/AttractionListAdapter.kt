package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemAttractionBinding
import com.example.trip.models.Attraction
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class AttractionListAdapter @Inject constructor(): ListAdapter<Attraction, AttractionListAdapter.AttractionViewHolder>(AttractionDiffUtil()) {

    private lateinit var attractionClickListener: AttractionClickListener

    fun setAttractionClickListener(attractionClickListener: AttractionClickListener) {
        this.attractionClickListener = attractionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        return AttractionViewHolder.create(parent, attractionClickListener)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttractionViewHolder(private val binding: ItemAttractionBinding, private val attractionClickListener: AttractionClickListener): RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: Attraction) {
            with(binding) {
                textName.text = attraction.name
                textAddress.text = attraction.address

                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val openingHour = attraction.openingHour.format(formatter)
                val closingHour = attraction.closingHour.format(formatter)

                textOpeningHours.text = itemView.resources.getString(R.string.text_from_to, openingHour, closingHour)

                if (attraction.isExpanded) {
                    buttonExpand.isSelected = true
                    extraLayout.setVisible()
                } else {
                    buttonExpand.isSelected = false
                    extraLayout.setGone()
                }

                if (bindingAdapterPosition == bindingAdapter?.itemCount?.minus(1)) {
                    imageNextAttraction.setGone()
                } else {
                    imageNextAttraction.setVisible()
                }
            }
            setOnSeeMoreClick(attraction.link)
            setOnExpandClick()
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


        companion object {
            fun create(parent: ViewGroup, attractionClickListener: AttractionClickListener): AttractionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionBinding.inflate(layoutInflater, parent, false)
                return AttractionViewHolder(
                    binding,
                    attractionClickListener
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
}