package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemAccommodationBinding
import com.example.trip.models.Accommodation
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import javax.inject.Inject


class AccommodationListAdapter @Inject constructor(): ListAdapter<Accommodation, AccommodationListAdapter.AccommodationViewHolder>(AccommodationDiffUtil()) {

    private lateinit var accommodationClickListener: AccommodationClickListener

    fun setAccommodationClickListener(accommodationClickListener: AccommodationClickListener) {
        this.accommodationClickListener = accommodationClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccommodationViewHolder {
        return AccommodationViewHolder.create(parent, accommodationClickListener)
    }

    override fun onBindViewHolder(holder: AccommodationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccommodationViewHolder(private val binding: ItemAccommodationBinding, private val accommodationClickListener: AccommodationClickListener): RecyclerView.ViewHolder(binding.root) {

        fun bind(accommodation: Accommodation) {
            with(binding) {
                textName.text = accommodation.name
                textAddress.text = accommodation.address
                textVotes.text = accommodation.votes.toString()
                textPrice.text =
                    itemView.resources.getString(R.string.text_pln, accommodation.price.toString())
                textDescription.text = accommodation.description

                buttonVote.isSelected = accommodation.isVoted

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
            setOnLinkClick()
            setOnTransportClick()
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

        private fun setOnLinkClick() {
            binding.buttonLink.setOnClickListener {
                accommodationClickListener.onLinkClick()
            }
        }

        private fun setOnTransportClick() {
            binding.buttonTransport.setOnClickListener {
                accommodationClickListener.onTransportClick()
            }
        }

        private fun setOnLongClick(accommodation: Accommodation) {
            binding.root.setOnLongClickListener {
                accommodationClickListener.onLongClick(accommodation, binding.root)
                true
            }
        }

        companion object {
            fun create(parent: ViewGroup, accommodationClickListener: AccommodationClickListener): AccommodationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAccommodationBinding.inflate(layoutInflater, parent, false)
                return AccommodationViewHolder(
                    binding,
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
    fun onLinkClick()
    fun onTransportClick()
    fun onLongClick(accommodation: Accommodation, view: View)
}