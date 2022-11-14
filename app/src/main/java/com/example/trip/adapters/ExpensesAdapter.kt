package com.example.trip.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trip.databinding.ItemExpenseBinding
import com.example.trip.models.Expense
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toStringFormat
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ExpensesAdapter @Inject constructor() :
    ListAdapter<Expense, ExpensesAdapter.ExpenseViewHolder>(
        ExpenseDiffUtil()
    ) {

    private lateinit var expenseClickListener: ExpenseClickListener

    private lateinit var currency: String

    fun setExpenseClickListener(expenseClickListener: ExpenseClickListener) {
        this.expenseClickListener = expenseClickListener
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder.create(parent, currency, expenseClickListener)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExpenseViewHolder(
        private val binding: ItemExpenseBinding,
        private val currency: String,
        private val expenseClickListener: ExpenseClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            val userId = 1L
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            with(binding) {
                textName.text = expense.title
                textBuyer.text = expense.creator.fullName
                textPrice.text = expense.price.toStringFormat(currency)
                textDate.text = expense.creationDate.format(formatter)
                expense.debtors.find { it.id == userId }?.let {
                    layoutContribution.setVisible()
                }?: layoutContribution.setGone()
            }
            setOnClick(expense)
        }


        private fun setOnClick(expense: Expense) {
            binding.root.setOnClickListener {
                expenseClickListener.onClick(expense)
            }
        }

        companion object {
            fun create(
                parent: ViewGroup,
                currency: String,
                expenseClickListener: ExpenseClickListener
            ): ExpenseViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemExpenseBinding.inflate(layoutInflater, parent, false)
                return ExpenseViewHolder(
                    binding,
                    currency,
                    expenseClickListener
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
    fun onClick(expense: Expense)
}