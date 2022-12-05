package com.example.trip.usecases.transport

import com.example.trip.dto.AirTransportDto
import com.example.trip.dto.CarTransportDto
import com.example.trip.dto.UserTransportDto
import com.example.trip.models.*
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.repositories.TransportRepository
import com.example.trip.utils.getMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetTransportUseCase @Inject constructor(
    private val transportRepository: TransportRepository,
    private val groupsRepository: GroupsRepository,
    private val accommodationsRepository: AccommodationsRepository
) {

    suspend operator fun invoke(accommodationId: Long, groupId: Long): Flow<Resource<Transport>> {
        return flow {
                emit(getTransport(accommodationId, groupId))
            }.catch {
                it.printStackTrace()
                if(it.cause is HttpException){
                    emit(Resource.Failure((it.cause as HttpException).code(), (it.cause as HttpException).response()?.getMessage()))
                } else {
                    emit(Resource.Failure(0))
                }
            }.onStart {
                emit(Resource.Loading())
            }
    }

    private suspend fun getTransport(accommodationId: Long, groupId: Long): Resource<Transport> {
        var airTransport: AirTransport? = null
        var carTransport: CarTransport? = null
        val userTransports = mutableListOf<UserTransport>()
        val accommodation = accommodationsRepository.getAccommodation(accommodationId)
        val group = groupsRepository.getGroup(groupId)
        val sourceLatLng = "${group.latitude},${group.longitude}"
        val destinationLatLng = "${accommodation.latitude},${accommodation.longitude}"

        transportRepository.getTransport(accommodationId).forEach {
            when (it) {
                is AirTransportDto -> {
                    airTransport = AirTransport(
                        it.transportId,
                        it.duration,
                        it.source,
                        it.destination,
                        it.link,
                        it.flight.map { flight ->
                            Flight(
                                flight.flightId,
                                flight.flightNumber,
                                flight.departureAirport,
                                flight.arrivalAirport,
                                flight.departureTime.toLocalTime(),
                                flight.arrivalTime.toLocalTime(),
                                flight.flightDuration,
                                flight.travelToAirportDuration,
                                flight.travelToAccommodationDuration
                            )
                        })
                }
                is CarTransportDto -> {
                    carTransport = CarTransport(
                        it.transportId,
                        it.duration,
                        sourceLatLng,
                        destinationLatLng,
                        it.distanceInKm
                    )
                }
                is UserTransportDto -> {
                    userTransports.add(
                        UserTransport(
                            it.transportId,
                            groupId,
                            accommodationId,
                            it.meanOfTransport.split(','),
                            it.duration,
                            it.meetingTime.toLocalDate(),
                            it.meetingTime.toLocalTime(),
                            it.price,
                            it.source,
                            it.destination,
                            it.description
                        )
                    )
                }
            }
        }
        return Resource.Success(Transport(carTransport, airTransport, userTransports))
    }

}