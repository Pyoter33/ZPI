package com.example.trip.utils

import com.example.trip.dto.GroupStage
import com.example.trip.dto.UserDto
import com.example.trip.models.GroupStatus
import com.example.trip.models.Participant
import com.example.trip.models.UserRole

fun UserDto.toParticipant(role: UserRole): Participant =
    Participant(
        userId,
        "$firstName $lastName",
        email,
        phoneNumber,
        role
    )

fun GroupStage.toGroupStatus(): GroupStatus {
    return when(this) {
        GroupStage.PLANNING_STAGE -> GroupStatus.PLANNING
        GroupStage.TRIP_STAGE -> GroupStatus.ONGOING
        GroupStage.AFTER_TRIP_STAGE -> GroupStatus.FINISHED
    }
}
