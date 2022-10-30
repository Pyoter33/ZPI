package com.example.trip.repositories

import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GroupsRepository @Inject constructor() {

    fun getGroups(userId: Long): Flow<Resource<List<Group>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Group(
                            1,
                            "Group 1",
                            GroupStatus.PLANNING,
                            "Location 1",
                            "PLN",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras efficitur consequat mollis. Proin aliquet turpis eu interdum consequat. Vestibulum id ex molestie, pharetra tellus sit amet, elementum odio. In hac habitasse platea dictumst. Cras porta tortor.",
                            6
                        ),
                        Group(
                            2,
                            "Group 2",
                            GroupStatus.ONGOING,
                            "Location 2",
                            "PLN",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras efficitur consequat mollis. Proin aliquet turpis eu interdum consequat. Vestibulum id ex molestie, pharetra tellus sit amet, elementum odio. In hac habitasse platea dictumst. Cras porta tortor.",
                            16
                        ),
                        Group(
                            3,
                            "Group 3",
                            GroupStatus.FINISHED,
                            "Location 3",
                            "PLN",
                            "Description",
                            11
                        ),
                        Group(
                            4,
                            "Group 4",
                            GroupStatus.PLANNING,
                            "Location 4",
                            "PLN",
                            "Description",
                            3
                        ),
                        Group(
                            5,
                            "Group 5",
                            GroupStatus.PLANNING,
                            "Location 5",
                            "PLN",
                            "Description",
                            5
                        ),
                    )
                )
            )
        }
    }

    suspend fun postGroup(userId: Long, group: Group): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun updateGroup(group: Group): Resource<Unit> {
        return Resource.Failure()
    }

}