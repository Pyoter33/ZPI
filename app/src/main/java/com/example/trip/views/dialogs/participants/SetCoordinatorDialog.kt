package com.example.trip.views.dialogs.participants

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Participant

class SetCoordinatorDialog(private val setCoordinatorDialogClickListener: SetCoordinatorDialogClickListener, private val participant: Participant) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.text_title_coordinate_dialog, participant.fullName))
            .setMessage(R.string.text_message_cannot_undo)
            .setPositiveButton(getString(R.string.text_accept)) { _, _ ->
                setCoordinatorDialogClickListener.onCoordinateClick(participant)
                dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "SetCoordinatorDialog"
    }

}

interface SetCoordinatorDialogClickListener {
    fun onCoordinateClick(participant: Participant)
}