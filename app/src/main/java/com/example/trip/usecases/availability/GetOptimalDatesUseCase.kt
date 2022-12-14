package com.example.trip.usecases.availability

import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetOptimalDatesUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository, private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<OptimalAvailability>>> {
        return flow {
            emit(getAvailabilities(groupId))
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code(), it.response()?.getMessage()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getAvailabilities(groupId: Long): Resource<List<OptimalAvailability>> {
        val group = groupsRepository.getGroup(groupId)
        val result = availabilityRepository.getOptimalDates(groupId).map {
            OptimalAvailability(
                Availability(
                    it.sharedGroupAvailabilityId,
                    -1,
                    it.dateFrom,
                    it.dateTo
                ),
                it.usersList.size,
                it.numberOfDays,
                it.sharedGroupAvailabilityId == group.selectedSharedAvailability
            )
        }.filter {
            it.users != 0
        }
        return Resource.Success(result)
    }

}