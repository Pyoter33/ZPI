package com.example.trip.views.dialogs.summary

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Availability
import com.example.trip.views.dialogs.availability.DeleteAvailabilityDialogClickListener

class DeleteAcceptedAvailabilityDialog(private val deleteDialogClickListener: DeleteAvailabilityDialogClickListener, private val availability: Availability) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_delete_dialog)
            .setPositiveButton(getString(R.string.text_delete)) { _, _ ->
                deleteDialogClickListener.onDeleteClick(availability)
                dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "DeleteAcceptedAvailabilityDialog"
    }

}