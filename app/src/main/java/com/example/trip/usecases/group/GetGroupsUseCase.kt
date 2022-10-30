package com.example.trip.usecases.group

import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupsUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    operator fun invoke(userId: Long): Flow<Resource<List<Group>>> {
        return groupsRepository.getGroups(userId)
    }

}