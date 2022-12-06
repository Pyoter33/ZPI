package com.example.trip.activities

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.example.trip.R
import com.example.trip.utils.setAppLocale
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity @Inject constructor(): AppCompatActivity() {

    companion object {
        private const val APP_LOCALE = "gb"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.setAppLocale(APP_LOCALE)))
    }

    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun showSnackbar(
        view: View,
        @StringRes messageResId: Int,
        @StringRes actionResId: Int,
        length: Int = Snackbar.LENGTH_LONG,
        action: () -> Unit
    ) {
        snackbar = Snackbar.make(view, messageResId, length)
            .setAction(actionResId) {
                action()
            }
            .setBackgroundTint(resources.getColor(R.color.grey400, null))
            .setTextColor(resources.getColor(R.color.black, null))
            .setActionTextColor(resources.getColor(R.color.primary, null))
        snackbar!!.show()
    }

    fun showSnackbar(
        view: View,
        message: String,
        @StringRes actionResId: Int,
        length: Int = Snackbar.LENGTH_LONG,
        action: () -> Unit
    ) {
        snackbar = Snackbar.make(view, message, length)
            .setAction(actionResId) {
                action()
            }
            .setBackgroundTint(resources.getColor(R.color.grey400, null))
            .setTextColor(resources.getColor(R.color.black, null))
            .setActionTextColor(resources.getColor(R.color.primary, null))
        snackbar!!.show()
    }


    fun hideSnackbar() {
        snackbar?.dismiss()
    }

}