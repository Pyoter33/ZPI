package com.example.trip.repositories

import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

class AvailabilityRepository @Inject constructor() {

    fun getAcceptedAvailability(groupId: Long): Flow<Resource<Availability?>> {
        return flow {
            emit(
                Resource.Success(
                    Availability(
                        1,
                        -1,
                        LocalDate.of(2022, 11, 18),
                        LocalDate.of(2022, 11, 20)
                    )
                )
            )
        }
    }

    suspend fun deleteAcceptedAvailability(availability: Availability): Resource<Unit> {
        return Resource.Failure()
    }

    fun getUserAvailabilities(userId: Long, groupId: Long): Flow<Resource<List<Availability>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Availability(
                            1,
                            userId,
                            LocalDate.of(2022, 11, 17),
                            LocalDate.of(2022, 11, 20)
                        ),
                        Availability(
                            2,
                            userId,
                            LocalDate.of(2022, 11, 24),
                            LocalDate.of(2022, 11, 27)
                        )
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postAvailability(availability: Availability): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun postAcceptedAvailability(availability: Availability): Resource<Unit> {
        return Resource.Success(Unit)
    }

    suspend fun deleteAvailability(id: Long): Resource<Unit> {
        return Resource.Failure()
    }

    fun getOptimalDates(groupId: Long): Flow<Resource<List<OptimalAvailability>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                       OptimalAvailability(
                            Availability(
                                1,
                                -1,
                                LocalDate.of(2022, 11, 18),
                                LocalDate.of(2022, 11, 20)
                            ), 4, 3
                        ),
                        OptimalAvailability(
                            Availability(
                                2,
                                -1,
                                LocalDate.of(2022, 11, 24),
                                LocalDate.of(2022, 11, 27)
                            ), 3, 4
                        )
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }
}