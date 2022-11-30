package com.example.trip.repositories

import com.example.trip.dto.*
import com.example.trip.service.AccommodationService
import com.example.trip.service.TripGroupService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import javax.inject.Inject


class AccommodationsRepository @Inject constructor(private val accommodationService: AccommodationService, private val groupService: TripGroupService) {

    suspend fun getAccommodationsList(groupId: Long): List<AccommodationDto> {
        return accommodationService.getAccommodationsList(groupId).toBodyOrError()
    }

    suspend fun getAccommodation(accommodationId: Long): AccommodationDto {
        return accommodationService.getAccommodation(accommodationId).toBodyOrError()
    }

    suspend fun postAccommodation(accommodationDto: AccommodationPostDto) {
        accommodationService.postAccommodation(accommodationDto).toNullableBodyOrError()
    }

    suspend fun updateAccommodation(
        accommodationId: Long,
        userId: Long,
        accommodationDto: AccommodationPostDto
    ) {
        accommodationService.updateAccommodation(accommodationId, userId, accommodationDto).toNullableBodyOrError()
    }

    suspend fun deleteAccommodation(accommodationId: Long) {
        accommodationService.deleteAccommodation(accommodationId).toNullableBodyOrError()
    }

    suspend fun getVotes(accommodationId: Long): List<AccommodationVoteDto> {
        return accommodationService.getVotes(accommodationId).toBodyOrError()
    }

    suspend fun postVote(accommodationVoteDto: AccommodationVotePostDto) {
        accommodationService.postVote(accommodationVoteDto).toNullableBodyOrError()
    }

    suspend fun deleteVote(accommodationVoteId: AccommodationVoteId) {
        accommodationService.deleteVote(accommodationVoteId).toNullableBodyOrError()
    }

    suspend fun postAcceptedAccommodation(accommodationId: Long) {
        accommodationService.acceptAccommodation(accommodationId).toNullableBodyOrError()
    }

    suspend fun deleteAcceptedAccommodation(groupId: Long) {
        groupService.deleteAcceptedAccommodation(groupId).toNullableBodyOrError()
    }
}