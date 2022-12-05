package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemGroupBinding
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.utils.setGone
import java.time.LocalDate
import javax.inject.Inject


class GroupsListAdapter @Inject constructor() :
    ListAdapter<Group, GroupsListAdapter.GroupsViewHolder>(GroupsDiffUtil()) {

    private lateinit var groupsClickListener: GroupsClickListener

    fun setGroupsClickListener(groupsClickListener: GroupsClickListener) {
        this.groupsClickListener = groupsClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder.create(parent, groupsClickListener)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GroupsViewHolder(
        private val binding: ItemGroupBinding,
        private val groupsClickListener: GroupsClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: Group) {
            with(binding) {
                textGroupName.text = group.name
                textStartLocation.text =
                    itemView.resources.getString(R.string.text_from, group.startCity)
                textParticipants.text = group.participantsNo.toString()
                textStatus.text =
                    when {
                        group.groupStatus == GroupStatus.PLANNING -> itemView.resources.getString(GroupStatus.PLANNING.resourceId)
                        group.groupStatus ==  GroupStatus.ONGOING && group.endDate?.isAfter(LocalDate.now()) == true -> itemView.resources.getString(GroupStatus.ONGOING.resourceId)
                        else -> itemView.resources.getString(GroupStatus.FINISHED.resourceId)
                    }
                if (group.description == null) {
                    buttonInfo.setGone()
                }
            }
            onClick(group)
            onInfoClick(group)
            setOnLongClick(group)
        }

        private fun onClick(group: Group) {
            binding.card.setOnClickListener {
                groupsClickListener.onClick(group)
            }
        }

        private fun onInfoClick(group: Group) {
            binding.buttonInfo.setOnClickListener {
                groupsClickListener.onInfoClick(group, it)
            }
        }

        private fun setOnLongClick(group: Group) {
            binding.card.setOnLongClickListener {
                groupsClickListener.onLongClick(group, it)
                true
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                groupsClickListener: GroupsClickListener
            ): GroupsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGroupBinding.inflate(layoutInflater, parent, false)
                return GroupsViewHolder(
                    binding,
                    groupsClickListener
                )
            }
        }
    }

}

class GroupsDiffUtil : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }
}

interface GroupsClickListener {
    fun onClick(group: Group)
    fun onLongClick(group: Group, view: View)
    fun onInfoClick(group: Group, view: View)
}