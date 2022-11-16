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

    fun getTransport(accommodationId: Long): Flow<Resource<Transport>> {
        return flow {
            emit(
                Resource.Success(
                    Transport(
                        carTransport = CarTransport(
                            0,
                            Duration.ofMinutes(235),
                            "51.1058831,17.0271114",
                            "50.0896109,14.4236038",
                            286
                        ),
                        airTransport = AirTransport(
                            1,
                            Duration.between(LocalTime.of(10, 20), LocalTime.of(15, 15)),
                            "Wrocław Airport",
                            "Prague Airport",
                            "",
                            listOf(
                                Flight(
                                    0,
                                    "LH1359",
                                    "WRO",
                                    "FRA",
                                    LocalTime.of(10, 50),
                                    LocalTime.of(12, 15),
                                    Duration.ofMinutes(85),
                                    Duration.ofMinutes(30),
                                    null
                                ),
                                Flight(
                                    1,
                                    "LH1400",
                                    "FRA",
                                    "PRG",
                                    LocalTime.of(13, 15),
                                    LocalTime.of(14, 15),
                                    Duration.ofMinutes(60),
                                    null,
                                    Duration.ofMinutes(60)
                                ),
                            )
                        ),
                        userTransport =
                        listOf(
                            UserTransport(
                                1,
                                1,
                                1,
                                listOf("Train"),
                                Duration.between(LocalTime.of(8, 30), LocalTime.of(14, 0)),
                                LocalDate.of(2022, 10, 17),
                                LocalTime.of(8, 15),
                                BigDecimal.valueOf(100),
                                "Wroclaw main railway station",
                                "Prague Sucha station",
                                "We have a direct connection to Prague by train. It is cheaper than other options!"
                            ),
                            UserTransport(
                                2,
                                1,
                                1,
                                listOf("FlixBus"),
                                Duration.between(LocalTime.of(6, 30), LocalTime.of(12, 30)),
                                LocalDate.of(2022, 10, 24),
                                LocalTime.of(6, 0),
                                BigDecimal.valueOf(120),
                                "Wroclaw Wroclavia shopping centre",
                                "Prague main bus station",
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