package com.example.trip.views.dialogs.finances

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Settlement

class ResolveSettlementDialog(private val resolveSettlementDialogClickListener: ResolveSettlementDialogClickListener, private val settlement: Settlement) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.text_title_resolve_dialog)
            .setMessage(R.string.text_message_cannot_undo)
            .setPositiveButton(getString(R.string.text_resolve)) { _, _ ->
                resolveSettlementDialogClickListener.onResolveClick(settlement)
                dismiss()
            }
            .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
            .create()
    }

    companion object {
        const val TAG = "DeleteAcceptedAvailabilityDialog"
    }

}

interface ResolveSettlementDialogClickListener {
    fun onResolveClick(settlement: Settlement)
}