package com.example.trip.usecases.summary

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.GroupsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetAcceptedAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository, private val groupsRepository: GroupsRepository) {

    operator fun invoke(groupId: Long): Flow<Resource<Accommodation?>> {
        return flow {
            emit(getAcceptedAccommodation(groupId))
        }.catch {
            it.printStackTrace()
            emit(Resource.Failure((it.cause as HttpException).code()))
        }.onStart {
            emit(Resource.Loading<Resource<Accommodation?>>() as Resource<Accommodation?>)
        }
    }

    private suspend fun getAcceptedAccommodation(groupId: Long): Resource<Accommodation?> {
        val accommodationId = groupsRepository.getGroup(groupId).selectedAccommodationId

        return Resource.Success(accommodationId?.let {
            val result = accommodationsRepository.getAcceptedAccommodation(it)
                Accommodation(
                    result.accommodationId,
                    result.groupId,
                    result.creator_id,
                    result.name,
                    result.streetAddress,
                    result.description,
                    result.imageLink,
                    result.sourceLink,
                    result.givenVotes,
                    result.price,
                    false
            )
        })
    }

}