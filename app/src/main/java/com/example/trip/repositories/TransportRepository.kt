package com.example.trip.repositories

import com.example.trip.dto.TransportDto
import com.example.trip.dto.UserTransportPostDto
import com.example.trip.models.Resource
import com.example.trip.service.TransportService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import javax.inject.Inject

class TransportRepository @Inject constructor(private val geoApiContext: GeoApiContext, private val transportService: TransportService) {

    suspend fun getTransport(accommodationId: Long): List<TransportDto> {
        return transportService.getTransport(accommodationId).toBodyOrError()
    }

    suspend fun postUserTransport(accommodationId:Long, userTransport: UserTransportPostDto) {
        transportService.postUserTransport(accommodationId, userTransport).toNullableBodyOrError()
    }

    suspend fun updateUserTransport(transportId: Long, userTransport: UserTransportPostDto) {
        transportService.updateUserTransport(transportId, userTransport).toNullableBodyOrError()
    }

    suspend fun deleteUserTransport(accommodationId: Long, transportId: Long) {
        transportService.deleteUserTransport(accommodationId, transportId)
    }

    suspend fun getRoute(
        origin: String,
        destination: String
    ): Resource<DirectionsResult> {
        val request = DirectionsApi.getDirections(geoApiContext, origin, destination)
        return request.awaitIgnoreError()?.let { Resource.Success(it) } ?: Resource.Failure()
    }

}