package com.example.trip.views.dialogs.accommodation

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Accommodation
import com.example.trip.utils.setDeleteDialog

class DeleteAccommodationDialog(private val deleteDialogClickListener: DeleteAccommodationDialogClickListener, private val accommodation: Accommodation) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteDialogClickListener.onDeleteClick(accommodation) }
    }

    companion object {
        const val TAG = "DeleteAccommodationDialog"
    }

}

interface DeleteAccommodationDialogClickListener {
    fun onDeleteClick(accommodation: Accommodation)
}