package com.example.trip.views.dialogs.availability

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Availability
import com.example.trip.utils.setDeleteDialog

class DeleteAvailabilityDialog(private val deleteDialogClickListener: DeleteAvailabilityDialogClickListener, private val availability: Availability) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteDialogClickListener.onDeleteClick(availability) }
    }

    companion object {
        const val TAG = "DeleteAvailabilityDialog"
    }

}

interface DeleteAvailabilityDialogClickListener {
    fun onDeleteClick(availability: Availability)
}