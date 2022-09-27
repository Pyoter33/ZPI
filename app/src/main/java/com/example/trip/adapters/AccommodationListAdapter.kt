package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemAccommodationBinding
import com.example.trip.models.Accommodation
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.skydoves.balloon.Balloon
import javax.inject.Inject


class AccommodationListAdapter @Inject constructor() :
    ListAdapter<Accommodation, AccommodationListAdapter.AccommodationViewHolder>(
        AccommodationDiffUtil()
    ) {

    private lateinit var accommodationClickListener: AccommodationClickListener

    private lateinit var popupMenu: Balloon

    fun setAccommodationClickListener(accommodationClickListener: AccommodationClickListener) {
        this.accommodationClickListener = accommodationClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccommodationViewHolder {
        return AccommodationViewHolder.create(parent, accommodationClickListener, popupMenu)
    }

    override fun onBindViewHolder(holder: AccommodationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccommodationViewHolder(
        private val binding: ItemAccommodationBinding,
        private val accommodationClickListener: AccommodationClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

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
                popupMenu.showAlignBottom(binding.card)
                setOnPopupButtonClick(R.id.button_accept){accommodationClickListener.onMenuAcceptClick(accommodation)}
                setOnPopupButtonClick(R.id.button_edit){accommodationClickListener.onMenuEditClick(accommodation)}
                setOnPopupButtonClick(R.id.button_delete){accommodationClickListener.onMenuDeleteClick(accommodation)}
                true
            }
        }

        private fun setOnPopupButtonClick(id: Int, action: () -> Unit) {
            popupMenu.getContentView().findViewById<Button>(id).setOnClickListener {
                action()
                popupMenu.dismiss()
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                accommodationClickListener: AccommodationClickListener,
                popupMenu: Balloon
            ): AccommodationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAccommodationBinding.inflate(layoutInflater, parent, false)
                return AccommodationViewHolder(
                    binding,
                    accommodationClickListener,
                    popupMenu
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
    fun onMenuAcceptClick(accommodation: Accommodation)
    fun onMenuEditClick(accommodation: Accommodation)
    fun onMenuDeleteClick(accommodation: Accommodation)
}