package com.example.trip.models

data class Group(
    val id: Int,
    val name: String,
    val groupStatus: GroupStatus,
    val participantsNo: Int
)
