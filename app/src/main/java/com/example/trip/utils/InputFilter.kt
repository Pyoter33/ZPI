package com.example.trip.utils

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeDecimal: Int, digitsAfterDecimal: Int) :
    InputFilter {
    var pattern: Pattern
    override fun filter(
        source: CharSequence,
        sourceStart: Int,
        sourceEnd: Int,
        destination: Spanned,
        destinationStart: Int,
        destinationEnd: Int
    ): CharSequence? {
        var newString =
            destination.toString().substring(0, destinationStart) + destination.toString()
                .substring(destinationEnd, destination.toString().length)
        newString =
            newString.substring(0, destinationStart) + source.toString() + newString.substring(
                destinationStart,
                newString.length
            )
        val matcher: Matcher = pattern.matcher(newString)
        return if (matcher.matches()) {
            null
        } else ""
    }

    init {
        pattern =
            Pattern.compile("(([1-9]{1}[0-9]{0," + (digitsBeforeDecimal - 1) + "})?||[0]{1})((\\.[0-9]{0," + digitsAfterDecimal + "})?)||(\\.)?")
    }
}