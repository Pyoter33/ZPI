package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject

class GetMapsRouteUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(
        origin: String,
        destination: String
    ): Resource<PolylineOptions> {
        val result = transportRepository.getRoute(origin, destination)
        val resultSuccess = (result.takeIf { it is Resource.Success } as? Resource.Success)?.data

        return resultSuccess?.let {
            val convertedList = it.routes.first().overviewPolyline.decodePath().map { latLang ->
                LatLng(latLang.lat, latLang.lng)
            }
            Resource.Success(PolylineOptions().apply { addAll(convertedList) })
        } ?: Resource.Failure()
    }

}