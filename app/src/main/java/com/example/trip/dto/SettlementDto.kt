package com.example.trip.dto

import com.example.trip.models.SettlementStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class SettlementDto(
    val financialRequestId: Long,
    val generationDate: LocalDateTime,
    val status: SettlementStatus,
    val amount: BigDecimal,
    val debtor: Long,
    val debtee: Long,
    val groupId: Long
)