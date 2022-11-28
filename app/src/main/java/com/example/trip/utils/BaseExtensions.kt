package com.example.trip.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Point
import android.icu.text.DecimalFormat
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trip.Constants
import com.example.trip.R
import com.skydoves.balloon.Balloon
import retrofit2.HttpException
import retrofit2.Response
import java.math.BigDecimal
import java.time.*
import java.util.*

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(@StringRes messageId: Int) =
    Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()

internal fun Context.displaySize(): Point {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun Context.setAppLocale(language: String): Context {
    val locale = Locale(language)

    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    return createConfigurationContext(config)
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(ClipboardManager::class.java) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
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

fun Fragment.getLongFromBundle(
    key: String
): Long {
    return requireActivity().intent.getLongExtra(key, -1L)
}

fun Fragment.getStringFromBundle(
    key: String
): String {
    return requireActivity().intent.getStringExtra(key) ?: "?"
}

fun Fragment.onBackArrowClick(view: View) {
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

fun Activity.onBackArrowClick(view: View) {
    view.setOnClickListener {
        onBackPressed()
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

fun Duration.toStringTime(): String {
    return "${toHours()}h ${toMinutesPart()}min"
}

fun BigDecimal.toStringFormat(currency: String): String {
    val df = DecimalFormat("0.00")
    return if (isIntegerValue(this)) {
        "${toInt()} $currency"
    } else {
        "${df.format(this)} $currency"
    }
}

fun BigDecimal.toStringFormat(): String {
    val df = DecimalFormat("0.00")
    return if (isIntegerValue(this)) {
        "${toInt()}"
    } else {
        df.format(this)
    }
}

private fun isIntegerValue(bd: BigDecimal): Boolean {
    return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0
}

fun String.formatPhone(): String {
    val split = split(' ')
    return "${split[0]} ${split[1].subSequence(0..2)} ${split[1].subSequence(3..5)} ${
        split[1].subSequence(
            6..8
        )
    }"
}

fun <T> Response<T>.toBodyOrError(): T {
    return when {
        isSuccessful -> {
            body()!!
        }
        else -> throw HttpException(this)
    }
}

fun <T> Response<T>.toNullableBodyOrError(): T? {
    return when {
        isSuccessful -> {
            body()
        }
        else -> throw HttpException(this)
    }
}

fun Balloon.setOnPopupButtonClick(@IdRes id: Int, action: () -> Unit) {
    getContentView().findViewById<Button>(id).setOnClickListener {
        action()
        dismiss()
    }
}

fun NavController.popBackStackWithRefresh() {
    previousBackStackEntry?.savedStateHandle?.set(Constants.TO_REFRESH_KEY, true)
    popBackStack()
}

fun NavController.popBackStackWithRefresh(@IdRes destinationId: Int, inclusive: Boolean) {
    getBackStackEntry(destinationId).savedStateHandle[Constants.TO_REFRESH_KEY] = true
    popBackStack(destinationId, inclusive)
}

fun Fragment.refreshIfNewData(action: () -> Unit) {
    if (findNavController().currentBackStackEntry?.savedStateHandle?.get<Boolean>(Constants.TO_REFRESH_KEY) == true) {
        action()
        findNavController().currentBackStackEntry?.savedStateHandle?.set(Constants.TO_REFRESH_KEY, null)
    }
}