package com.example.trip.usecases.group

import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import javax.inject.Inject

class PostGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(userId: Int, group: Group): Resource<Unit> {
        return groupsRepository.postGroup(userId, group)
    }

}