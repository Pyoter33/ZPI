package com.example.trip.usecases.group

import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.SharedPreferencesHelper
import com.example.trip.utils.toGroupStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(
    private val groupsRepository: GroupsRepository,
    private val preferencesHelper: SharedPreferencesHelper
) {

    suspend operator fun invoke(): Flow<Resource<List<Group>>> {
        return flow {
            emit(getGroups())
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getGroups(): Resource<List<Group>> =
        Resource.Success(groupsRepository.getGroups(preferencesHelper.getUserId()).map {
            val coordinators = groupsRepository.getCoordinators(it.groupId).map { it.userId }
            Group(
                it.groupId,
                it.name,
                it.groupStage.toGroupStatus(),
                it.startLocation,
                it.currency.currencyCode,
                it.minimalNumberOfParticipants,
                it.minimalNumberOfDays,
                it.description,
                it.participantsNum,
                it.startDate,
                it.endDate,
                coordinators
            )
        })

}