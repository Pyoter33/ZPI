package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemDayPlanBinding
import com.example.trip.models.DayPlan
import com.example.trip.models.DayPlanIcon
import com.skydoves.balloon.Balloon
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class DayPlansAdapter @Inject constructor() :
    ListAdapter<DayPlan, DayPlansAdapter.DayPlansViewHolder>(DayPlansDiffUtil()) {

    private lateinit var dayPlansClickListener: DayPlansClickListener

    private lateinit var popupMenu: Balloon

    fun setDayPlansClickListener(dayPlansClickListener: DayPlansClickListener) {
        this.dayPlansClickListener = dayPlansClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayPlansViewHolder {
        return DayPlansViewHolder.create(parent, dayPlansClickListener, popupMenu)
    }

    override fun onBindViewHolder(holder: DayPlansViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayPlansViewHolder(
        private val binding: ItemDayPlanBinding,
        private val dayPlansClickListener: DayPlansClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dayPlan: DayPlan) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

            with(dayPlan) {
                binding.textDate.text = date.format(formatter)
                binding.textName.text = name
                binding.textAttractions.text = attractionsNumber.toString()

                when(iconCode) {
                    DayPlanIcon.BOAT.code -> binding.imageIcon.setImageResource(DayPlanIcon.BOAT.resId)
                    DayPlanIcon.CASTLE.code -> binding.imageIcon.setImageResource(DayPlanIcon.CASTLE.resId)
                    DayPlanIcon.CITY.code -> binding.imageIcon.setImageResource(DayPlanIcon.CITY.resId)
                    DayPlanIcon.WALK.code -> binding.imageIcon.setImageResource(DayPlanIcon.WALK.resId)
                    DayPlanIcon.WATER.code -> binding.imageIcon.setImageResource(DayPlanIcon.WATER.resId)
                    DayPlanIcon.MONUMENT.code -> binding.imageIcon.setImageResource(DayPlanIcon.MONUMENT.resId)
                    DayPlanIcon.MOUNTAIN.code -> binding.imageIcon.setImageResource(DayPlanIcon.MOUNTAIN.resId)
                    DayPlanIcon.RESTAURANT.code -> binding.imageIcon.setImageResource(DayPlanIcon.RESTAURANT.resId)
                }

                setOnLongClick(this)
                setOnClickListener(this)
            }
        }

        private fun setOnClickListener(dayPlan: DayPlan) {
            binding.card.setOnClickListener {
                dayPlansClickListener.onClick(dayPlan)
            }
        }

        private fun setOnLongClick(dayPlan: DayPlan) {
            binding.card.setOnLongClickListener {
                popupMenu.showAlignBottom(binding.card)
                setOnPopupButtonClick(R.id.button_edit) {
                    dayPlansClickListener.onMenuEditClick(
                        dayPlan
                    )
                }
                setOnPopupButtonClick(R.id.button_delete) {
                    dayPlansClickListener.onMenuDeleteClick(
                        dayPlan
                    )
                }
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
                dayPlansClickListener: DayPlansClickListener,
                popupMenu: Balloon
            ): DayPlansViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDayPlanBinding.inflate(layoutInflater, parent, false)
                return DayPlansViewHolder(
                    binding,
                    dayPlansClickListener,
                    popupMenu
                )
            }
        }

    }

}

class DayPlansDiffUtil : DiffUtil.ItemCallback<DayPlan>() {
    override fun areItemsTheSame(oldItem: DayPlan, newItem: DayPlan): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DayPlan, newItem: DayPlan): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface DayPlansClickListener {
    fun onClick(dayPlan: DayPlan)
    fun onMenuEditClick(dayPlan: DayPlan)
    fun onMenuDeleteClick(dayPlan: DayPlan)
}