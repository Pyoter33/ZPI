package com.example.trip.usecases.summary

import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import retrofit2.HttpException
import javax.inject.Inject

class GetAcceptedAvailabilityUseCase @Inject constructor(
    private val availabilityRepository: AvailabilityRepository,
    private val groupsRepository: GroupsRepository
) {
    suspend operator fun invoke(groupId: Long): Resource<OptimalAvailability?> {
        return try {
            getAcceptedAvailability(groupId)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

    private suspend fun getAcceptedAvailability(groupId: Long): Resource<OptimalAvailability?> {
        val availabilityId = groupsRepository.getGroup(groupId).selectedSharedAvailability

        return Resource.Success(availabilityId?.let {
            val result = availabilityRepository.getAcceptedAvailability(it)
            OptimalAvailability(
                Availability(
                    result.sharedGroupAvailabilityId,
                    -1,
                    result.dateFrom,
                    result.dateTo
                ),
                result.usersList.size,
                result.numberOfDays
            )
        })
    }

}