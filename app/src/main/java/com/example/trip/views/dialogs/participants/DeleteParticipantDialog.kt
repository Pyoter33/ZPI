package com.example.trip.views.dialogs.participants

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Participant
import com.example.trip.utils.setDeleteDialog

class DeleteParticipantDialog(
    private val deleteParticipantDialogClickListener: DeleteParticipantDialogClickListener,
    private val participant: Participant
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteParticipantDialogClickListener.onDeleteClick(participant) }
    }

    companion object {
        const val TAG = "DeleteParticipantDialog"
    }

}

interface DeleteParticipantDialogClickListener {
    fun onDeleteClick(participant: Participant)
}