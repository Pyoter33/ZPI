package com.example.trip.views.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Group
import com.example.trip.utils.setDeleteDialog

class DeleteGroupDialog(
    private val deleteGroupDialogClickListener: DeleteGroupDialogClickListener,
    private val group: Group
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteGroupDialogClickListener.onDeleteClick(group) }
    }

    companion object {
        const val TAG = "DeleteGroupDialog"
    }

}

interface DeleteGroupDialogClickListener {
    fun onDeleteClick(group: Group)
}