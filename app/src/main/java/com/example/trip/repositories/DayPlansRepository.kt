package com.example.trip.repositories

import com.example.trip.models.Attraction
import com.example.trip.models.AttractionPreview
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class DayPlansRepository @Inject constructor() {

    fun getDayPlans(groupId: Int): Flow<Resource<List<DayPlan>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        DayPlan(0, 0, "Day plan 1", LocalDate.of(2022, 10, 11), 5, 3),
                        DayPlan(0, 0, "Day plan 2", LocalDate.of(2022, 10, 12), 5, 4),
                        DayPlan(0, 0, "Day plan 3", LocalDate.of(2022, 10, 13), 14, 6),
                        DayPlan(0, 0, "Day plan 4", LocalDate.of(2022, 10, 14), 5, 2),
                        DayPlan(0, 0, "Day plan 5", LocalDate.of(2022, 10, 15), 5, 3),
                    )
                )
            )
        }
    }

    suspend fun postDayPlan(dayPlan: DayPlan): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun updateDayPlan(dayPlan: DayPlan): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun postAttraction(attraction: Attraction): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun updateAttraction(attraction: Attraction): Resource<Unit> {
        return Resource.Failure()
    }

    fun getAttractionsForQuery(query: String): Flow<Resource<List<AttractionPreview>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        AttractionPreview(
                            0,
                            "Attraction 1",
                            "Address 1",
                            "Desc",
                            ""
                        ),
                        AttractionPreview(
                            0,
                            "Attraction 2",
                            "Address 2",
                            "Desc",
                            ""
                        ),
                        AttractionPreview(
                            0,
                            "Attraction 3",
                            "Address 3",
                            "Desc",
                            ""
                        ),
                        AttractionPreview(
                            0,
                            "Attraction 4",
                            "Address 4",
                            "Desc",
                            ""
                        ),
                        AttractionPreview(
                            0,
                            "Attraction 5",
                            "Address 5",
                            "Desc",
                            "",
                        )
                    )
                )
            )
        }
    }


    fun getAttractionsForDayPlan(groupId: Int, dayPlanId: Int): Flow<Resource<List<Attraction>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Attraction(
                            1,
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
                            2,
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
                            3,
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
                            4,
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
                            5,
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