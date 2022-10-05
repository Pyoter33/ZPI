package com.example.trip.repositories

import com.example.trip.models.Attraction
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DayPlansRepository @Inject constructor() {

    fun getDayPlans(groupId: Int): Flow<Resource<List<DayPlan>>> {
        return emptyFlow()
    }

    fun getAttractionsForDayPlan(groupId: Int, dayPlanId: Int): Flow<Resource<List<Attraction>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 1",
                            "Address 1",
                            "Desc",
                            "",
                            "",
                            10.0
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 2",
                            "Address 2",
                            "Desc",
                            "",
                            "",
                            11.4
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 3",
                            "Address 3",
                            "Desc",
                            "",
                            "",
                            0.5
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 4",
                            "Address 4",
                            "Desc",
                            "",
                            "",
                            1.4
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 5",
                            "Address 5",
                            "Desc",
                            "",
                            "",
                            null
                        )
                    )
                )
            )
        }
    }

}