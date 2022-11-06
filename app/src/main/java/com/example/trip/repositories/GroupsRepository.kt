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
                            "Weekend trip to Prague",
                            GroupStatus.PLANNING,
                            "Wroclaw",
                            "PLN",
                            3,
                            2,
                            "Weekend in Prague, sightseeing, knedlicky eating, beer drinking and chill!",
                            4
                        ),
                        Group(
                            2,
                            "Holidays in Spain",
                            GroupStatus.ONGOING,
                            "Warsaw",
                            "PLN",
                            6,
                            7,
                            "We start in Madrid, than Barcelona, Valencia, Malaga. A lot of sightseeing and lying on the beach. Bring your suncream!",
                            10
                        ),
                        Group(
                            3,
                            "Excursion to Dresden",
                            GroupStatus.FINISHED,
                            "Wroclaw",
                            "PLN",
                            3,
                            2,
                            "Let's see all the museums!!!",
                            3
                        )
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