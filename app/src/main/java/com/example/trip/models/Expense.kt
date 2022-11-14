package com.example.trip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.time.LocalDate

@Parcelize
data class Expense(
    val id: Long,
    val groupId: Long,
    val creator: Participant,
    val creationDate: LocalDate,
    val title: String,
    val price: BigDecimal,
    val debtors: List<Participant>
): Parcelable