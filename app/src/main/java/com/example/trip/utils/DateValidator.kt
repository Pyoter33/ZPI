package com.example.trip.utils

import com.example.trip.models.Availability
import com.google.android.material.datepicker.CalendarConstraints
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
class DateValidator(private val rangesList: @RawValue List<Availability>) :
    CalendarConstraints.DateValidator {

    private val disabledDatesLong: List<Pair<Long, Long>> = rangesList.map {
        Pair(
            it.startDate.apply { minusDays(1) }.toMillis(),
            it.endDate.toMillis()
        )
    }

    override fun isValid(date: Long): Boolean {
        for (range in disabledDatesLong) {
            if (date in range.first..range.second) {
                return false
            }
        }
        return true
    }

    override fun describeContents(): Int {
        return 0
    }

    fun areDatesCorrect(startDate: Long, endDate: Long): Boolean {
        for (range in disabledDatesLong) {
            if (startDate in range.first..range.second || endDate in range.first..range.second || (startDate < range.first && endDate > range.second)) {
                return false
            }
        }
        return true
    }
}
