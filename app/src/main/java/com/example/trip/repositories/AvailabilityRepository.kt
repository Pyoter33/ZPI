package com.example.trip.repositories

import com.example.trip.dto.AvailabilityDto
import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.dto.SharedGroupAvailabilityDto
import com.example.trip.models.Availability
import com.example.trip.service.AvailabilityService
import com.example.trip.service.TripGroupService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import javax.inject.Inject

class AvailabilityRepository @Inject constructor(private val availabilityService: AvailabilityService, private val tripGroupService: TripGroupService) {

    suspend fun getAcceptedAvailability(sharedAvailabilityId: Long): SharedGroupAvailabilityDto {
        return availabilityService.getAcceptedAvailability(sharedAvailabilityId).toBodyOrError()
    }

    suspend fun postAcceptedAvailability(groupId: Long, availability: Availability) {
        availabilityService.postAcceptedAvailability(
            availability.startDate,
            availability.endDate,
            groupId
        ).toNullableBodyOrError()
    }

    suspend fun acceptAvailability(sharedGroupAvailabilityId: Long) {
        availabilityService.acceptAvailability(sharedGroupAvailabilityId).toNullableBodyOrError()
    }

    suspend fun deleteAcceptedAvailability(groupId: Long) {
        tripGroupService.deleteAcceptedAvailability(groupId).toNullableBodyOrError()
    }

    suspend fun getUserAvailabilities(userId: Long, groupId: Long): List<AvailabilityDto> {
        return availabilityService.getAvailabilitiesForUser(userId, groupId).toBodyOrError()
    }

    suspend fun postAvailability(availabilityPostDto: AvailabilityPostDto) {
        availabilityService.postAvailability(availabilityPostDto).toNullableBodyOrError()
    }

    suspend fun deleteAvailability(availabilityId: Long, groupId: Long) {
        availabilityService.deleteAvailability(availabilityId, groupId).toNullableBodyOrError()
    }

    suspend fun getOptimalDates(groupId: Long): List<SharedGroupAvailabilityDto> {
        return availabilityService.getAvailabilitiesForGroup(groupId).toBodyOrError()
    }
}