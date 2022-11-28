package com.example.trip.utils.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal

class BigDecimalAdapter {

    @FromJson
    fun decode(string: String): BigDecimal {
        return string.toBigDecimal()
    }

    @ToJson
    fun encode(bigDecimal: BigDecimal): String {
        return bigDecimal.toPlainString()
    }

}