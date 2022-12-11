package com.example.trip.usecases.accommodation

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.SharedPreferencesHelper
import com.example.trip.utils.getMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetAccommodationsListUseCase @Inject constructor(
    private val accommodationsRepository: AccommodationsRepository,
    private val groupsRepository: GroupsRepository,
    private val preferencesHelper: SharedPreferencesHelper
) {
    suspend operator fun invoke(groupId: Long): Flow<Resource<List<Accommodation>>> {
        return flow {
            emit(getAccommodations(groupId))
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code(), it.response()?.getMessage()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getAccommodations(groupId: Long): Resource<List<Accommodation>> {
        val group = groupsRepository.getGroup(groupId)
        val result = accommodationsRepository.getAccommodationsList(groupId).map {
            val votes = accommodationsRepository.getVotes(it.accommodationId)
                .map { vote -> vote.id.userId }
            Accommodation(
                it.accommodationId,
                it.groupId,
                it.creator_id,
                it.name,
                it.streetAddress,
                it.description,
                it.imageLink,
                it.sourceLink,
                it.givenVotes,
                it.price,
                preferencesHelper.getUserId() in votes,
                it.accommodationId == group.selectedAccommodationId
            )
        }
        return Resource.Success(result)
    }
}