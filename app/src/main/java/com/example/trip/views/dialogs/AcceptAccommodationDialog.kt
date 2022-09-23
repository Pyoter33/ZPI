package com.example.trip.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Accommodation

class AcceptAccommodationDialog(
    private val acceptDialogClickListener: AcceptAccommodationDialogClickListener,
    private val accommodation: Accommodation
) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_accept_dialog)
            .setMessage(getString(R.string.text_message_cannot_undo))
            .setPositiveButton(getString(R.string.text_accept)) { _, _ ->
                acceptDialogClickListener.onAcceptClick(
                    accommodation
                ); dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "AcceptDialog"
    }

}

interface AcceptAccommodationDialogClickListener {
    fun onAcceptClick(accommodation: Accommodation)
}