package com.example.trip.repositories

import com.example.trip.models.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class TransportRepository @Inject constructor(private val geoApiContext: GeoApiContext) {

    fun getTransport(groupId: Long, accommodationId: Long): Flow<Resource<Transport>> {
        return flow {
            emit(
                Resource.Success(
                    Transport(
                        carTransport = CarTransport(
                            0,
                            Duration.between(LocalTime.of(16, 30), LocalTime.of(18, 55)),
                            "51.06546,16.9834855",
                            "54.406386,18.555113",//"41.3926467,2.0701498",
                            1500
                        ),
                        airTransport = AirTransport(
                            1,
                            Duration.between(LocalTime.of(16, 0), LocalTime.of(22, 55)),
                            "Wrocław Airport",
                            "Barcelona Airport",
                            "",
                            listOf(
                                Flight(
                                    0,
                                    "FV12345",
                                    "WRO",
                                    "MUN",
                                    LocalTime.of(16, 30),
                                    LocalTime.of(18, 30),
                                    Duration.between(LocalTime.of(16, 30), LocalTime.of(18, 30)),
                                    Duration.between(LocalTime.of(16, 0), LocalTime.of(16, 30)),
                                    null
                                ),
                                Flight(
                                    1,
                                    "FV62346",
                                    "MUN",
                                    "BAR",
                                    LocalTime.of(20, 0),
                                    LocalTime.of(21, 55),
                                    Duration.between(LocalTime.of(20, 0), LocalTime.of(21, 55)),
                                    Duration.between(LocalTime.of(21, 55), LocalTime.of(22, 55)),
                                    null
                                ),

                                )
                        ),
                        userTransport =
                        listOf(
                            UserTransport(
                                1,
                                1,
                                1,
                                listOf("Bus", "Train"),
                                Duration.between(LocalTime.of(6, 30), LocalTime.of(22, 30)),
                                LocalDate.of(2022, 10, 11),
                                LocalTime.of(6, 30),
                                BigDecimal.valueOf(200),
                                "Wroclaw main railway station",
                                "Barcelona main bus station",
                                "We start with the bus from Wroclaw to Berlin and than directly to Barcelona by train."
                            ),
                            UserTransport(
                                2,
                                1,
                                1,
                                listOf("Bus"),
                                Duration.between(LocalTime.of(3, 30), LocalTime.of(22, 30)),
                                LocalDate.of(2022, 10, 17),
                                LocalTime.of(3, 30),
                                BigDecimal.valueOf(300),
                                "Wroclaw Wroclavia shopping centre",
                                "Barcelona city centre",
                                null
                            )
                        )
                    )
                )
            )
        }
    }

    suspend fun postUserTransport(userTransport: UserTransport): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun updateUserTransport(userTransport: UserTransport): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun deleteUserTransport(id: Long): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun getRoute(
        origin: String,
        destination: String
    ): Resource<DirectionsResult> {
        val request = DirectionsApi.getDirections(geoApiContext, origin, destination)
        return request.awaitIgnoreError()?.let { Resource.Success(it) } ?: Resource.Failure()
    }

}