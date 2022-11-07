package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.R
import com.example.trip.databinding.ItemExpenseBinding
import com.example.trip.models.Expense
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.skydoves.balloon.Balloon
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExpensesAdapter @Inject constructor() :
    ListAdapter<Expense, ExpensesAdapter.ExpenseViewHolder>(
        ExpenseDiffUtil()
    ) {

    private lateinit var expenseClickListener: ExpenseClickListener

    private lateinit var popupMenu: Balloon

    fun setExpenseClickListener(expenseClickListener: ExpenseClickListener) {
        this.expenseClickListener = expenseClickListener
    }

    fun setPopupMenu(popupMenu: Balloon) {
        this.popupMenu = popupMenu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder.create(parent, expenseClickListener, popupMenu)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExpenseViewHolder(
        private val binding: ItemExpenseBinding,
        private val expenseClickListener: ExpenseClickListener,
        private val popupMenu: Balloon
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            val userId = 1L
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            with(binding) {
                textName.text = expense.title
                textBuyer.text = expense.creator.fullName
                textPrice.text = expense.price.toString() + "PLN"
                textDate.text = expense.creationDate.format(formatter)
                expense.debtors.find { it.id == userId }?.let {
                    layoutContribution.setVisible()
                }?: layoutContribution.setGone()
            }
            setOnLongClick(expense)
        }

        private fun setOnLongClick(expense: Expense) {
            binding.root.setOnLongClickListener {
                popupMenu.showAlignBottom(binding.root)
                setOnPopupButtonClick(R.id.button_edit){expenseClickListener.onMenuEditClick(expense)}
                setOnPopupButtonClick(R.id.button_delete){expenseClickListener.onMenuDeleteClick(expense)}
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
                expenseClickListener: ExpenseClickListener,
                popupMenu: Balloon
            ): ExpenseViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemExpenseBinding.inflate(layoutInflater, parent, false)
                return ExpenseViewHolder(
                    binding,
                    expenseClickListener,
                    popupMenu
                )
            }
        }
    }
}

class ExpenseDiffUtil : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id && oldItem.groupId == newItem.groupId
    }
}

interface ExpenseClickListener {
    fun onMenuEditClick(expense: Expense)
    fun onMenuDeleteClick(expense: Expense)
}