package com.example.trip.repositories

import com.example.trip.models.Attraction
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.time.LocalTime
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
                            LocalTime.of(10, 0),
                            LocalTime.of(19, 0),
                            "",
                            ""
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 2",
                            "Address 2",
                            "Desc",
                            LocalTime.of(11, 0),
                            LocalTime.of(20, 0),
                            "",
                            ""
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 3",
                            "Address 3",
                            "Desc",
                            LocalTime.of(13, 0),
                            LocalTime.of(22, 15),
                            "",
                            ""
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 4",
                            "Address 4",
                            "Desc",
                            LocalTime.of(7, 0),
                            LocalTime.of(15, 0),
                            "",
                            ""
                        ),
                        Attraction(
                            0,
                            0,
                            0,
                            "Attraction 5",
                            "Address 5",
                            "Desc",
                            LocalTime.of(19, 0),
                            LocalTime.of(3, 30),
                            "",
                            ""
                        )
                    )
                )
            )
        }
    }

}