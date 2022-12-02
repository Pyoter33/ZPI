package com.example.trip.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R

class TransportDialog() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_transport_dialog)
            .setMessage(R.string.text_message_cannot_generate)
            .setPositiveButton(R.string.text_ok) { _, _ ->
                dismiss()
            }.create()
    }

    companion object {
        const val TAG = "AvailabilityNeededDialog"
    }

}
