package com.example.trip.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trip.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes messageId: Int) =
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)

    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return createConfigurationContext(config)
}

fun LocalDate.toMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Fragment.setSwipeRefreshLayout(
    layoutRefresh: SwipeRefreshLayout,
    @ColorRes color: Int,
    action: () -> Unit
) {
    layoutRefresh.setColorSchemeResources(color)
    layoutRefresh.setOnRefreshListener {
        action()
    }
}

fun View.animateSlideTransition(gravity: Int, parent: ViewGroup, duration: Long) {
    val transition: Transition = Slide(gravity)
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent, transition)
}

fun View.animateFadeTransition(parent: ViewGroup, duration: Long) {
    val transition: Transition = Fade()
    transition.duration = duration
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(parent, transition)
}

fun DialogFragment.setAcceptDialog(action: () -> Unit): Dialog {
    return AlertDialog.Builder(requireContext())
        .setTitle(R.string.text_title_accept_dialog)
        .setMessage(getString(R.string.text_message_cannot_undo))
        .setPositiveButton(getString(R.string.text_accept)) { _, _ ->
            action()
            dismiss()
        }
        .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
        .create()
}

fun DialogFragment.setDeleteDialog(action: () -> Unit): Dialog {
    return AlertDialog.Builder(requireContext())
        .setTitle(R.string.text_title_delete_dialog)
        .setMessage(getString(R.string.text_message_cannot_undo))
        .setPositiveButton(getString(R.string.text_delete)) { _, _ ->
            action()
            dismiss()
        }
        .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> dismiss() }
        .create()
}