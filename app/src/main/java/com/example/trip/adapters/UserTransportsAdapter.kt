package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemUserTransportBinding
import com.example.trip.models.UserTransport
import com.example.trip.utils.toStringTime
import com.skydoves.balloon.Balloon
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UserTransportsAdapter @Inject constructor() :
    ListAdapter<UserTransport, UserTransportsAdapter.UserTransportViewHolder>(
        UserTransportDiffUtil()
    ) {

    private lateinit var userTransportClickLister: UserTransportClickListener

    private lateinit var popupMenu: Balloon

    fun setUserTransportClickListener(userTransportClickLister: UserTransportClickListener) {
        this.userTransportClickLister = userTransportClickLister
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTransportViewHolder {
        return UserTransportViewHolder.create(parent, userTransportClickLister, popupMenu)
    }

    override fun onBindViewHolder(holder: UserTransportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserTransportViewHolder(
        private val binding: ItemUserTransportBinding,
        private val userTransportClickLister: UserTransportClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(userTransport: UserTransport) {
            with(binding) {
                val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                val textAdapter = TextAdapter()

                listTransportations.adapter = textAdapter
                textAdapter.submitList(userTransport.meansOfTransport)
                textSource.text = userTransport.source
                textDestination.text = userTransport.destination
                textDuration.text = userTransport.duration.toStringTime()
                textDate.text = userTransport.meetingDate.format(dateFormatter)
                textMeeting.text = itemView.resources.getString(R.string.text_meeting_at, userTransport.meetingTime.format(timeFormatter))
                textPrice.text = userTransport.price.toString()
                textDescription.text = userTransport.description
            }

            setOnLongClick(userTransport)
        }

        private fun setOnLongClick(userTransport: UserTransport) {
            binding.root.setOnLongClickListener {
                popupMenu.showAlignBottom(binding.root)
                setOnPopupButtonClick(R.id.button_edit){userTransportClickLister.onMenuEditClick(userTransport)}
                setOnPopupButtonClick(R.id.button_delete){userTransportClickLister.onMenuDeleteClick(userTransport)}
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
                userTransportClickLister: UserTransportClickListener,
                popupMenu: Balloon
            ): UserTransportViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserTransportBinding.inflate(layoutInflater, parent, false)
                return UserTransportViewHolder(
                    binding,
                    userTransportClickLister,
                    popupMenu
                )
            }
        }
    }
}

class UserTransportDiffUtil : DiffUtil.ItemCallback<UserTransport>() {
    override fun areItemsTheSame(oldItem: UserTransport, newItem: UserTransport): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: UserTransport, newItem: UserTransport): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface UserTransportClickListener {
    fun onMenuEditClick(userTransport: UserTransport)
    fun onMenuDeleteClick(userTransport: UserTransport)
}