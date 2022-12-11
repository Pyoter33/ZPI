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
        return transportRepository.getRoute(origin, destination)?.let {
            val convertedList = it.routes.firstOrNull()?.overviewPolyline?.decodePath()?.map { latLang ->
                LatLng(latLang.lat, latLang.lng)
            }
            convertedList?.let {
                Resource.Success(PolylineOptions().apply { addAll(it) })
            } ?: Resource.Failure()
        } ?: Resource.Failure()
    }

}