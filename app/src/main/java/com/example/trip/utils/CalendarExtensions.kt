package com.example.trip.utils

import android.graphics.Typeface
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.ColorRes
import com.example.trip.R
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous

fun CalendarView.setMonthHeaderBinder() {
    monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
        override fun create(view: View) = MonthViewContainer(view)
        override fun bind(container: MonthViewContainer, month: CalendarMonth) {}
    }
}

fun CalendarView.onNextMonthClick(button: ImageButton) {
    button.setOnClickListener {
        findFirstVisibleMonth()?.let {
            smoothScrollToMonth(it.yearMonth.next)
        }
    }
}

fun CalendarView.onPreviousMonthClick(button: ImageButton) {
    button.setOnClickListener {
        findFirstVisibleMonth()?.let {
            smoothScrollToMonth(it.yearMonth.previous)
        }
    }
}

fun CalendarView.setMonthScrollListener(textCurrentMonth: TextView) {
    monthScrollListener = { month ->
        val title = "${month.yearMonth.month} ${month.yearMonth.year}"
        textCurrentMonth.text = title
    }
}

fun CalendarView.setDayBinder(
    dateValidator: DateValidator,
    @ColorRes mainColor: Int,
    @ColorRes mainColorTransparent: Int
) {
    dayBinder = object : DayBinder<DayViewContainer> {
        override fun create(view: View) = DayViewContainer(view)

        override fun bind(container: DayViewContainer, day: CalendarDay) {
            val localDate = day.date

            with(container.binding) {
                calendarDayText.text = day.date.dayOfMonth.toString()

                if (!dateValidator.isValid(localDate.toMillis())) {
                    if (day.owner != DayOwner.THIS_MONTH) {
                        setChosenDate(calendarDayText, underline, mainColorTransparent)
                    } else {
                        setChosenDate(calendarDayText, underline, mainColor)
                    }
                } else {
                    if (day.owner != DayOwner.THIS_MONTH) {
                        setNormalDate(calendarDayText, underline, R.color.grey300)
                    } else {
                        setNormalDate(calendarDayText, underline, R.color.grey700)
                    }
                }
            }
        }

    }
}

private fun CalendarView.setChosenDate(
    calendarDayText: TextView,
    underline: FrameLayout,
    @ColorRes color: Int
) {
    calendarDayText.typeface = Typeface.DEFAULT_BOLD
    underline.setVisible()
    underline.setBackgroundResource(color)
    calendarDayText.setTextColor(
        resources.getColor(
            color,
            null
        )
    )
}

private fun CalendarView.setNormalDate(
    calendarDayText: TextView,
    underline: FrameLayout,
    @ColorRes color: Int
) {
    calendarDayText.typeface = Typeface.DEFAULT
    underline.setGone()
    calendarDayText.setTextColor(
        resources.getColor(
            color,
            null
        )
    )
}