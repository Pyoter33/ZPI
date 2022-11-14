package com.example.trip.adapters

import android.text.InputFilter
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.example.trip.utils.DecimalDigitsInputFilter


@BindingAdapter("app:digitsBeforeZero", "app:digitsAfterZero")
fun bindAmountInputFilter(view: EditText, digitsBeforeZero: Int, digitsAfterZero: Int) {
    view.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(digitsBeforeZero, digitsAfterZero))
}