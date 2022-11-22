package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trip.databinding.ItemAccommodationBinding
import com.example.trip.models.Accommodation
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toStringFormat
import javax.inject.Inject


class AccommodationListAdapter @Inject constructor() :
    ListAdapter<Accommodation, AccommodationListAdapter.AccommodationViewHolder>(
        AccommodationDiffUtil()
    ) {

    private lateinit var accommodationClickListener: AccommodationClickListener

    private lateinit var currency: String

    fun setAccommodationClickListener(accommodationClickListener: AccommodationClickListener) {
        this.accommodationClickListener = accommodationClickListener
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccommodationViewHolder {
        return AccommodationViewHolder.create(parent, currency, accommodationClickListener)
    }

    override fun onBindViewHolder(holder: AccommodationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccommodationViewHolder(
        private val binding: ItemAccommodationBinding,
        private val currency: String,
        private val accommodationClickListener: AccommodationClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(accommodation: Accommodation) {
            with(binding) {
                textName.text = accommodation.name
                textAddress.text = accommodation.address
                textVotes.text = accommodation.votes.toString()
                textPrice.text = accommodation.price.toStringFormat(currency)
                textDescription.text = accommodation.description

                buttonVote.isSelected = accommodation.isVoted

                Glide.with(itemView).load(accommodation.imageUrl).centerCrop().into(binding.imageAccommodation)

                if (accommodation.description.isNullOrEmpty()) {
                    buttonExpand.setGone()
                } else {
                    buttonExpand.setVisible()
                }

                if (accommodation.isExpanded) {
                    buttonExpand.isSelected = true
                    extraLayout.setVisible()
                } else {
                    buttonExpand.isSelected = false
                    extraLayout.setGone()
                }

            }
            setOnExpandClick()
            setOnVoteClick(binding.buttonVote)
            setOnLinkClick(accommodation)
            setOnTransportClick(accommodation)
            setOnLongClick(accommodation)
        }

        private fun setOnVoteClick(button: View) {
            binding.buttonVote.setOnClickListener {
                accommodationClickListener.onVoteClick(bindingAdapterPosition, button)
            }
        }

        private fun setOnExpandClick() {
            binding.buttonExpand.setOnClickListener {
                accommodationClickListener.onExpandClick(bindingAdapterPosition)
            }
        }

        private fun setOnLinkClick(accommodation: Accommodation) {
            binding.buttonLink.setOnClickListener {
                accommodationClickListener.onLinkClick(accommodation)
            }
        }

        private fun setOnTransportClick(accommodation: Accommodation) {
            binding.buttonTransport.setOnClickListener {
                accommodationClickListener.onTransportClick(accommodation)
            }
        }

        private fun setOnLongClick(accommodation: Accommodation) {
            binding.root.setOnLongClickListener {
                accommodationClickListener.onLongClick(accommodation, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String,
                accommodationClickListener: AccommodationClickListener
            ): AccommodationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAccommodationBinding.inflate(layoutInflater, parent, false)
                return AccommodationViewHolder(
                    binding,
                    currency,
                    accommodationClickListener
                )
            }
        }
    }
}

class AccommodationDiffUtil : DiffUtil.ItemCallback<Accommodation>() {
    override fun areItemsTheSame(oldItem: Accommodation, newItem: Accommodation): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Accommodation, newItem: Accommodation): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface AccommodationClickListener {
    fun onExpandClick(position: Int)
    fun onVoteClick(position: Int, button: View)
    fun onLinkClick(accommodation: Accommodation)
    fun onTransportClick(accommodation: Accommodation)
    fun onLongClick(accommodation: Accommodation, view: View)
}