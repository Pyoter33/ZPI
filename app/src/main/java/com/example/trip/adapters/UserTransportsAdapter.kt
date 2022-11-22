package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemUserTransportBinding
import com.example.trip.models.UserTransport
import com.example.trip.utils.toStringFormat
import com.example.trip.utils.toStringTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UserTransportsAdapter @Inject constructor() :
    ListAdapter<UserTransport, UserTransportsAdapter.UserTransportViewHolder>(
        UserTransportDiffUtil()
    ) {

    private lateinit var userTransportClickLister: UserTransportClickListener

    private lateinit var currency: String

    fun setUserTransportClickListener(userTransportClickLister: UserTransportClickListener) {
        this.userTransportClickLister = userTransportClickLister
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTransportViewHolder {
        return UserTransportViewHolder.create(parent, currency, userTransportClickLister)
    }

    override fun onBindViewHolder(holder: UserTransportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserTransportViewHolder(
        private val binding: ItemUserTransportBinding,
        private val currency: String,
        private val userTransportClickListener: UserTransportClickListener
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
                textPrice.text = userTransport.price.toStringFormat(currency)
                textDescription.text = userTransport.description
            }

            setOnLongClick(userTransport)
        }

        private fun setOnLongClick(userTransport: UserTransport) {
            binding.root.setOnLongClickListener {
                userTransportClickListener.onLongClick(userTransport, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String,
                userTransportClickLister: UserTransportClickListener
            ): UserTransportViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserTransportBinding.inflate(layoutInflater, parent, false)
                return UserTransportViewHolder(
                    binding,
                    currency,
                    userTransportClickLister
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
    fun onLongClick(userTransport: UserTransport, view: View)
}