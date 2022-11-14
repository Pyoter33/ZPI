package com.example.trip.models

import java.math.BigDecimal

data class CheckableParticipant(
    val participant: Participant,
    var amount: BigDecimal,
    var isChecked: Boolean
)
