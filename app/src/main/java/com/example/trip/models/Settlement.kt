package com.example.trip.models

import java.math.BigDecimal

data class Settlement(
    val id: Long,
    val groupId: Long,
    var status: SettlementStatus,
    val amount: BigDecimal,
    val debtor: Participant,
    val debtee: Participant
)