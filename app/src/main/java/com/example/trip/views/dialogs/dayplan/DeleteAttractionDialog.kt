package com.example.trip.views.dialogs.dayplan

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.Attraction
import com.example.trip.utils.setDeleteDialog

class DeleteAttractionDialog(
    private val deleteAttractionDialogClickListener: DeleteAttractionDialogClickListener,
    private val attraction: Attraction
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteAttractionDialogClickListener.onDeleteClick(attraction) }
    }

    companion object {
        const val TAG = "DeleteAttractionDialog"
    }

}

interface DeleteAttractionDialogClickListener {
    fun onDeleteClick(attraction: Attraction)
}