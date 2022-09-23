package com.example.trip.utils

import android.view.View
import com.example.trip.databinding.LayoutCalendarDayBinding
import com.example.trip.databinding.LayoutCalendarHeaderBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
     val binding = LayoutCalendarDayBinding.bind(view)
}

class MonthViewContainer(view: View) : ViewContainer(view) {
     val layout = LayoutCalendarHeaderBinding.bind(view).legendLayout
}