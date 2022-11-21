package com.example.trip.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R

class LeaveGroupDialog(private val leaveGroupDialogClickListener: LeaveGroupDialogClickListener) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_leave_dialog)
            .setMessage(R.string.text_message_cannot_undo)
            .setPositiveButton(getString(R.string.text_leave)) { _, _ ->
                leaveGroupDialogClickListener.onLeaveClick()
                dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "LeaveGroupDialog"
    }

}

interface LeaveGroupDialogClickListener {
    fun onLeaveClick()
}