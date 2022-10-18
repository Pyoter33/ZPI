package com.example.trip.views.dialogs.participants

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.trip.R
import com.example.trip.models.Resource
import com.example.trip.utils.setGone
import com.example.trip.utils.setVisible
import com.example.trip.utils.toast
import com.example.trip.viewmodels.participants.ParticipantsViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class GetInviteLinkDialog(
    private val viewModel: ParticipantsViewModel,
    private val getInviteLinkDialogClickListener: GetInviteLinkDialogClickListener
) : DialogFragment() {

    private lateinit var text: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogLayout = inflater.inflate(R.layout.layout_dialog_copy, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogLayout)
            .create()

        observeLink(dialogLayout)
        setOnCopyClick(dialogLayout)

        return dialog
    }


    private fun observeLink(dialogLayout: View) {
        val editText = dialogLayout.findViewById<MaterialAutoCompleteTextView>(R.id.editText_link)
        val buttonCopy = dialogLayout.findViewById<Button>(R.id.button_copy)
        val progressIndicator =
            dialogLayout.findViewById<CircularProgressIndicator>(R.id.circularProgressIndicator)
        viewModel.inviteLink.observe(this) {
            when (it) {
                is Resource.Success -> {
                    editText.setText(it.data)
                    text = it.data
                    buttonCopy.isEnabled = true
                    progressIndicator.setGone()
                }
                is Resource.Loading -> {
                    buttonCopy.isEnabled = false
                    progressIndicator.setVisible()
                }
                is Resource.Failure -> {
                    dialog?.dismiss()
                    requireContext().toast(R.string.text_fetch_failure)
                    progressIndicator.setGone()
                    buttonCopy.isEnabled = false
                }
            }
        }
    }

    private fun setOnCopyClick(dialogLayout: View) {
        val buttonCopy = dialogLayout.findViewById<Button>(R.id.button_copy)
        buttonCopy.setOnClickListener {
            getInviteLinkDialogClickListener.onCopyClick(text)
            dialog?.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.inviteLink.removeObservers(this)
    }

    companion object {
        const val TAG = "GetInviteLinkDialog"
    }

}

interface GetInviteLinkDialogClickListener {
    fun onCopyClick(link: String)
}