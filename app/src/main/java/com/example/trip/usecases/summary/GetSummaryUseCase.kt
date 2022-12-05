package com.example.trip.usecases.summary

import com.example.trip.models.*
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val availabilityRepository: AvailabilityRepository,
    private val accommodationsRepository: AccommodationsRepository,
    private val participantsRepository: ParticipantsRepository,
    private val groupsRepository: GroupsRepository
) {
    operator fun invoke(groupId: Long): Flow<Resource<Summary>> {
        return flow {
            emit(getSummary(groupId))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading<Resource<Summary>>() as Resource<Summary>)
        }
    }

    private suspend fun getSummary(groupId: Long): Resource<Summary> {
        val accommodationId = groupsRepository.getGroup(groupId).selectedAccommodationId
        val accommodation = accommodationId?.let {
            val result = accommodationsRepository.getAccommodation(it)
            Accommodation(
                result.accommodationId,
                result.groupId,
                result.creator_id,
                result.name,
                result.streetAddress,
                result.description,
                result.imageLink,
                result.sourceLink,
                result.givenVotes,
                result.price,
                false,
                isAccepted = true
            )
        }

        val availabilityId = groupsRepository.getGroup(groupId).selectedSharedAvailability
        val availability = availabilityId?.let {
            val result = availabilityRepository.getAcceptedAvailability(it)
            OptimalAvailability(
                Availability(
                    result.sharedGroupAvailabilityId,
                    -1,
                    result.dateFrom,
                    result.dateTo
                ),
                result.usersList.size,
                result.numberOfDays,
                true
            )
        }
        val coordinatorsIds =  groupsRepository.getCoordinators(groupId).map { it.userId }
        val participants = participantsRepository.getParticipantsForGroup(groupId).map {
            Participant(
                it.userId,
                "${it.firstName} ${it.surname}",
                it.email,
                it.phoneNumber,
                if (it.userId in coordinatorsIds) UserRole.COORDINATOR else UserRole.BASIC_USER
            )
        }

        return Resource.Success(Summary(accommodation, availability, participants))
    }

}