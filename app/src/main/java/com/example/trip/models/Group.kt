package com.example.trip.models

data class Group(
    val id: Int,
    val name: String,
    val groupStatus: GroupStatus,
    val startLocation: String,
    val currency: String,
    val maxVotes: Int,
    val description: String,
    val participantsNo: Int
)
