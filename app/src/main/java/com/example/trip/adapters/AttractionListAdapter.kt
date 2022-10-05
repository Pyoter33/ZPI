package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemAttractionBinding
import com.example.trip.databinding.ItemAttractionMirrorBinding
import com.example.trip.models.Attraction
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.skydoves.balloon.Balloon
import javax.inject.Inject


class AttractionListAdapter @Inject constructor() :
    ListAdapter<Attraction, RecyclerView.ViewHolder>(AttractionDiffUtil()) {

    private lateinit var attractionClickListener: AttractionClickListener

    private lateinit var popupMenu: Balloon

    fun setAttractionClickListener(attractionClickListener: AttractionClickListener) {
        this.attractionClickListener = attractionClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            0 -> AttractionViewHolder.create(parent, attractionClickListener, popupMenu)
            else -> AttractionMirrorViewHolder.create(parent, attractionClickListener, popupMenu)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AttractionViewHolder) {
            holder.bind(getItem(position))
        } else {
            (holder as AttractionMirrorViewHolder).bind(getItem(position))
        }
    }

    class AttractionViewHolder(
        private val binding: ItemAttractionBinding,
        private val attractionClickListener: AttractionClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: Attraction) {
            with(binding) {
                textName.text = attraction.name
                textAddress.text = attraction.address
                textItemNumber.text = itemView.resources.getString(R.string.format_dot, bindingAdapterPosition + 1)
                textDistance.text = itemView.resources.getString(R.string.format_km, attraction.distanceToNext)

                imageTransport.isSelected = attraction.distanceToNext < 3

                if (attraction.isExpanded) {
                    buttonExpand.isSelected = true
                    extraLayout.setVisible()
                } else {
                    buttonExpand.isSelected = false
                    extraLayout.setGone()
                }

                if (bindingAdapterPosition == bindingAdapter?.itemCount?.minus(1)) {
                    layoutNextAttraction.setGone()
                } else {
                    layoutNextAttraction.setVisible()
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
                popupMenu.showAlignBottom(itemView)
                setOnPopupButtonClick(R.id.button_edit){attractionClickListener.onMenuEditClick(attraction)}
                setOnPopupButtonClick(R.id.button_delete){attractionClickListener.onMenuDeleteClick(attraction)}
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
                attractionClickListener: AttractionClickListener,
                popupMenu: Balloon
                ): AttractionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionBinding.inflate(layoutInflater, parent, false)
                return AttractionViewHolder(
                    binding,
                    attractionClickListener,
                    popupMenu
                )
            }
        }
    }

    class AttractionMirrorViewHolder(
        private val binding: ItemAttractionMirrorBinding,
        private val attractionClickListener: AttractionClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(attraction: Attraction) {
            with(binding) {
                textName.text = attraction.name
                textAddress.text = attraction.address
                textItemNumber.text = itemView.resources.getString(R.string.format_dot, bindingAdapterPosition + 1)
                textDistance.text = itemView.resources.getString(R.string.format_km, attraction.distanceToNext)

                imageTransport.isSelected = attraction.distanceToNext < 3

                if (attraction.isExpanded) {
                    buttonExpand.isSelected = true
                    extraLayout.setVisible()
                } else {
                    buttonExpand.isSelected = false
                    extraLayout.setGone()
                }

                if (bindingAdapterPosition == bindingAdapter?.itemCount?.minus(1)) {
                    layoutNextAttraction.setGone()
                } else {
                    layoutNextAttraction.setVisible()
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
                popupMenu.showAlignBottom(itemView)
                setOnPopupButtonClick(R.id.button_edit){attractionClickListener.onMenuEditClick(attraction)}
                setOnPopupButtonClick(R.id.button_delete){attractionClickListener.onMenuDeleteClick(attraction)}
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
                attractionClickListener: AttractionClickListener,
                popupMenu: Balloon
            ): AttractionMirrorViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAttractionMirrorBinding.inflate(layoutInflater, parent, false)
                return AttractionMirrorViewHolder(
                    binding,
                    attractionClickListener,
                    popupMenu
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
    fun onMenuEditClick(attraction: Attraction)
    fun onMenuDeleteClick(attraction: Attraction)
}