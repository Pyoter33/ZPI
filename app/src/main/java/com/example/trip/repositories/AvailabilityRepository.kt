package com.example.trip.repositories

import com.example.trip.models.Availability
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
                        LocalDate.of(2022, 10, 10),
                        LocalDate.of(2022, 10, 14)
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
                            LocalDate.of(2022, 10, 10),
                            LocalDate.of(2022, 10, 14)
                        ),
                        Availability(
                            2,
                            userId,
                            LocalDate.of(2022, 10, 17),
                            LocalDate.of(2022, 10, 20)
                        ),
                        Availability(
                            3,
                            userId,
                            LocalDate.of(2022, 10, 28),
                            LocalDate.of(2022, 11, 4),
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

    fun getOptimalDates(groupId: Long): Flow<Resource<List<Pair<Availability, Int>>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Pair(
                            Availability(
                                1,
                                -1,
                                LocalDate.of(2022, 12, 2),
                                LocalDate.of(2022, 12, 11)
                            ), 8
                        ),
                        Pair(
                            Availability(
                                2,
                                -1,
                                LocalDate.of(2022, 12, 2),
                                LocalDate.of(2022, 12, 10)
                            ), 9
                        ),
                        Pair(
                            Availability(
                                3,
                                -1,
                                LocalDate.of(2022, 12, 2),
                                LocalDate.of(2022, 12, 17)
                            ), 7
                        ),
                        Pair(
                            Availability(
                                4,
                                -1,
                                LocalDate.of(2022, 12, 2),
                                LocalDate.of(2022, 12, 15)
                            ), 9
                        )
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }
}