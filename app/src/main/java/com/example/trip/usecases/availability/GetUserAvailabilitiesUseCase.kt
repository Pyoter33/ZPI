package com.example.trip.usecases.availability

import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import com.example.trip.utils.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetUserAvailabilitiesUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(userId: Long, groupId: Long): Flow<Resource<List<Availability>>> {
        return flow {
            emit(getAvailabilities(userId, groupId))
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

    private suspend fun getAvailabilities(
        userId: Long,
        groupId: Long
    ): Resource<List<Availability>> =
        Resource.Success(availabilityRepository.getUserAvailabilities(userId, groupId).map {
            Availability(
                it.availabilityId,
                it.userId,
                it.dateFrom,
                it.dateTo
            )
        }
        )

}