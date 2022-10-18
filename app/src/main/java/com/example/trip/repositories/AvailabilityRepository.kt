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

    fun getUserAvailabilities(userId: Int, groupId: Int): Flow<Resource<List<Availability>>> {
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

    suspend fun deleteAvailability(id: Int): Resource<Unit> {
        return Resource.Failure()
    }

    fun getOptimalDates(groupId: Int): Flow<Resource<Pair<Availability, Int>>> {
        return flow {
            emit(
                Resource.Success(
                    Pair(
                        Availability(1, -1, LocalDate.of(2022, 12, 2), LocalDate.of(2022, 12, 11)), 8
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }
}