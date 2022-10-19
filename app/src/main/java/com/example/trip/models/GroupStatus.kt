package com.example.trip.models

import androidx.annotation.StringRes
import com.example.trip.R

enum class GroupStatus(val code: Int, @StringRes var resourceId: Int) {
    PLANNING(0, R.string.text_planning),
    ONGOING(1, R.string.text_ongoing),
    FINISHED(2, R.string.text_finished)
}
