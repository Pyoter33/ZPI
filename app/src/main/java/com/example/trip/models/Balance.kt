package com.example.trip.models

import java.math.BigDecimal

data class Balance(
    val participant: Participant,
    val amount: BigDecimal,
    val maxAmount: BigDecimal,
    val status: BalanceStatus
)