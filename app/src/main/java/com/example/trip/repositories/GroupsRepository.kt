package com.example.trip.repositories
import com.example.trip.dto.TripGroupPostDto
import com.example.trip.dto.UserDto
import com.example.trip.dto.UserGroupDto
import com.example.trip.service.TripGroupService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import javax.inject.Inject

class GroupsRepository @Inject constructor(private val tripGroupService: TripGroupService) {

    suspend fun getGroups(userId: Long): List<UserGroupDto> {
        return tripGroupService.getGroups(userId).toBodyOrError()
    }

    suspend fun getGroup(groupId: Long): UserGroupDto {
        return tripGroupService.getGroupById(groupId).toBodyOrError()
    }

    suspend fun getCoordinators(groupId: Long): List<UserDto> {
        return tripGroupService.getCoordinators(groupId).toBodyOrError()
    }

    suspend fun postGroup(tripGroupPostDto: TripGroupPostDto) {
        tripGroupService.postGroup(tripGroupPostDto).toNullableBodyOrError()
    }

    suspend fun updateGroup(groupId: Long, tripGroupPostDto: TripGroupPostDto) {
        tripGroupService.updateGroup(groupId, tripGroupPostDto).toNullableBodyOrError()
    }

    suspend fun deleteGroup(groupId: Long) {
        tripGroupService.deleteGroup(groupId).toNullableBodyOrError()
    }

    suspend fun leaveGroup(groupId: Long) {
        tripGroupService.leaveGroup(groupId).toNullableBodyOrError()
    }

    suspend fun changeGroupState(groupId: Long) {
        tripGroupService.changeGroupStage(groupId).toNullableBodyOrError()
    }

}