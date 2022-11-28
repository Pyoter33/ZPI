package com.example.trip.usecases.group

import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.toGroupStatus
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class GetGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(groupId: Long): Resource<Group> {
        return try {
           getGroup(groupId)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

    private suspend fun getGroup(groupId: Long): Resource<Group> {
        val group = groupsRepository.getGroup(groupId)
        val coordinators = groupsRepository.getCoordinators(groupId).map { it.userId }
        val result = Group(
            groupId,
            group.name,
            group.groupStage.toGroupStatus(),
            group.startCity,
            group.currency.currencyCode,
            group.minimalNumberOfParticipants,
            group.minimalNumberOfDays,
            group.description,
            group.participantsNum,
            coordinators
        )
        return Resource.Success(result)
    }

}