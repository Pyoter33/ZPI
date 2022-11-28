package com.example.trip.usecases.availability

import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetOptimalDatesUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(groupId: Long): Flow<Resource<List<OptimalAvailability>>> {
        return flow {
            emit(getAvailabilities(groupId))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getAvailabilities(groupId: Long): Resource<List<OptimalAvailability>> =
        Resource.Success(availabilityRepository.getOptimalDates(groupId).map {
            OptimalAvailability(
                Availability(
                    it.sharedGroupAvailabilityId,
                    -1,
                    it.dateFrom,
                    it.dateTo
                ),
                it.usersList.size,
                it.numberOfDays
            )
        }.filter {
            it.users != 0
        }
        )

}