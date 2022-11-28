package com.example.trip.utils.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

class CurrencyAdapter {

        @FromJson
        fun decode(currencyString: String): Currency {
            return Currency.getInstance(currencyString)
        }

        @ToJson
        fun encode(currency: Currency): String {
            return currency.currencyCode
        }

    }
