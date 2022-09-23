package com.example.trip.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Accommodation

class DeleteAccommodationDialog(private val deleteDialogClickListener: DeleteAccommodationDialogClickListener, private val accommodation: Accommodation) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_delete_dialog)
            .setMessage(getString(R.string.text_message_cannot_undo))
            .setPositiveButton(getString(R.string.text_delete)) { _, _ -> deleteDialogClickListener.onDeleteClick(accommodation); dismiss() }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "DeleteDialog"
    }

}

interface DeleteAccommodationDialogClickListener {
    fun onDeleteClick(accommodation: Accommodation)
}