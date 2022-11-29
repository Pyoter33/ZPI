package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.models.Transport
import com.example.trip.repositories.TransportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(accommodationId: Long): Flow<Resource<Transport>> {

        return flowOf(Resource.Failure())

//        return flow {
//                emit(getGroups())
//            }.catch {
//                it.printStackTrace()
//                if(it.cause is HttpException){
//                    emit(Resource.Failure((it.cause as HttpException).code()))
//                } else {
//                    emit(Resource.Failure(0))
//                }
//            }.onStart {
//                emit(Resource.Loading())
//            }
        }

//    private suspend fun getTransport(accommodationId: Long): Resource<Transport> {
//        val airTransport = transportRepository.getTransport(accommodationId)[0].let {
//            AirTransport(
//                it.transportId,
//                it.duration,
//                it.source,
//                it.destination,
//                it.link,
//                (it as AirTransportDto).flight.map { flight ->
//                    Flight(
//                        flight.flightId,
//                        flight.flightNumber,
//                        flight.departureAirport,
//                        flight.arrivalAirport,
//                        flight.departureTime.toLocalTime(),
//                        flight.arrivalTime.toLocalTime(),
//                        flight.flightDuration,
//                        flight.travelToAirportDuration,
//                        flight.travelToAccommodationDuration
//                    )
//                }
//            )
//        }
        //val carTransport = transportRepository.



}