package com.example.trip.usecases.summary

import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetAcceptedAvailabilityUseCaseFlow @Inject constructor(
    private val availabilityRepository: AvailabilityRepository,
    private val groupsRepository: GroupsRepository
) {
    operator fun invoke(groupId: Long): Flow<Resource<OptimalAvailability?>> {
        return flow {
            emit(getAcceptedAvailability(groupId))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading<Resource<OptimalAvailability?>>() as Resource<OptimalAvailability?>)
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