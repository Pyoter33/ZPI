package com.example.trip.usecases.dayplan

import com.example.trip.Constants
import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class GetAttractionPreviewUseCase @Inject constructor(
    private val dayPlansRepository: DayPlansRepository,
    @Named(Constants.GOOGLE_API_KEY) private val apiKey: String?
) {

    suspend operator fun invoke(query: String): Flow<Resource<List<AttractionPreview>>> {
        return flow {
            emit(getAttractions(query))
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

    private suspend fun getAttractions(query: String): Resource<List<AttractionPreview>> =
        Resource.Success(dayPlansRepository.getAttractionsForQuery(query).map {
            AttractionPreview(
                it.attractionName,
                it.address,
                it.latitude,
                it.longitude,
                it.placeId,
                getPhotoUrl(it.photoLink),
                it.photoLink,
                it.url
            )
        })

    private fun getPhotoUrl(reference: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photo_reference=$reference&key=$apiKey"
    }

}