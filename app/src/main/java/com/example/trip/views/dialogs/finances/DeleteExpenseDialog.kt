package com.example.trip.views.dialogs.finances


import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Expense
import com.example.trip.utils.setDeleteDialog

class DeleteExpenseDialog(private val deleteExpenseDialogClickListener: DeleteExpenseDialogClickListener, private val expense: Expense) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteExpenseDialogClickListener.onDeleteClick(expense) }
    }

    companion object {
        const val TAG = "DeleteExpenseDialog"
    }

}

interface DeleteExpenseDialogClickListener {
    fun onDeleteClick(expense: Expense)
}