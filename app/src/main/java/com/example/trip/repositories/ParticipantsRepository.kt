package com.example.trip.repositories

import com.example.trip.Constants
import com.example.trip.dto.AppUserDto
import com.example.trip.dto.UserDto
import com.example.trip.service.TripGroupService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import retrofit2.HttpException
import javax.inject.Inject

class ParticipantsRepository @Inject constructor(private val tripGroupService: TripGroupService) {

    suspend fun getInviteLink(groupId: Long): String {
        val result = tripGroupService.getInvitation(groupId)
        return result.headers()[Constants.LOCATION_KEY] ?: throw HttpException(result)
    }

    suspend fun updateParticipant(appUserDto: AppUserDto) {
        tripGroupService.updateUser(appUserDto).toNullableBodyOrError()
    }

    suspend fun getUser(userId: Long): AppUserDto {
        return tripGroupService.getUser(userId).toBodyOrError()
    }

    suspend fun getParticipantsForGroup(groupId: Long): List<UserDto> {
        return tripGroupService.getParticipants(groupId).toBodyOrError()
    }

    suspend fun postCoordinator(groupId: Long, userId: Long) {
        tripGroupService.putCoordinator(groupId, userId).toNullableBodyOrError()
    }

    suspend fun deleteParticipant(groupId: Long, userId: Long) {
        tripGroupService.deleteParticipant(groupId, userId).toNullableBodyOrError()
    }

}