package com.example.trip.views.dialogs.dayplan

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trip.models.DayPlan
import com.example.trip.utils.setDeleteDialog

class DeleteDayPlanDialog(
    private val deleteDayPlanDialogClickListener: DeleteDayPlanDialogClickListener,
    private val dayPlan: DayPlan
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return setDeleteDialog { deleteDayPlanDialogClickListener.onDeleteClick(dayPlan) }
    }

    companion object {
        const val TAG = "DeleteDayPlanDialog"
    }

}

interface DeleteDayPlanDialogClickListener {
    fun onDeleteClick(dayPlan: DayPlan)
}