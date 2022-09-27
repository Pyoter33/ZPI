package com.example.trip.views.dialogs.accommodation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Accommodation
import com.example.trip.utils.setAcceptDialog

class AcceptAccommodationDialog(
    private val acceptDialogClickListener: AcceptAccommodationDialogClickListener,
    private val accommodation: Accommodation
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setAcceptDialog { acceptDialogClickListener.onAcceptClick(accommodation) }
    }

    companion object {
        const val TAG = "AcceptAccommodationDialog"
    }

}

interface AcceptAccommodationDialogClickListener {
    fun onAcceptClick(accommodation: Accommodation)
}