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

    fun getDayPlans(groupId: Long): Flow<Resource<List<DayPlan>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        DayPlan(1, 0, "Sightseeing", LocalDate.of(2022, 11, 7), 5, 0),
                        DayPlan(2, 0, "Boat cruise", LocalDate.of(2022, 11, 8), 3, 6),
                        DayPlan(3, 0, "Suburbs trip", LocalDate.of(2022, 11, 9), 6, 1),
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

    suspend fun deleteAttraction(id: Long, groupId: Long): Resource<Unit> {
        return Resource.Failure()
    }

    fun getAttractionsForQuery(query: String): Flow<Resource<List<AttractionPreview>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        AttractionPreview(
                            1,
                            "ILUZIONE Barcelona",
                            "C/ de Ferran Agulló, 18, 08021 Barcelona, Spain",
                            "https://lh3.googleusercontent.com/p/AF1QipOeY4Jk6aotxujsc1kNC_SyxwzCy32uJ4Yh69pm=s0",
                            "https://goo.gl/maps/nkNmCwDs9ohgFfgh8"
                        ),
                        AttractionPreview(
                            2,
                            "a Restaurant",
                            "Plaça de Sant Felip Neri, 2, 08002 Barcelona, Spain",
                            "https://lh3.googleusercontent.com/p/AF1QipM85qyn1Rjm8qk_E4QsI46O1OYNBEcz_jwo61dg=s0",
                            "https://goo.gl/maps/pHdoa1od2ThwvG8v9"
                        ),
                        AttractionPreview(
                            3,
                            "Restaurant La Rioja",
                            "Carrer de Duran i Bas, 5, Bajos, 08002 Barcelona, Spain",
                            "https://lh3.googleusercontent.com/p/AF1QipMCQ-5o7N8k4w3OrpjCV2DGt0wOkY8jDJtVzSuK=s0",
                            "https://g.page/lariojarestaurante?share"
                        ),
                        AttractionPreview(
                            4,
                            "Manairó Restaurant",
                            "C/ de la Diputació, 424, 08013 Barcelona, Spain",
                            "https://lh3.googleusercontent.com/p/AF1QipOQ7pjk792IJx-Ap7Vg0CSr77g0haAtnXrmPqrD=s0",
                            "https://goo.gl/maps/5Qe7aBQUYz6Rtd4b6"
                        ),
                        AttractionPreview(
                            5,
                            "Con Gracia",
                            "C. de Martínez de la Rosa, 8, 08012 Barcelona, Spain",
                            "https://lh3.googleusercontent.com/p/AF1QipPcxBt5JBAA92JZw6QFEk5tUEcLb1Rj-FT6R0on=s0",
                            "https://g.page/congraciarestaurant?share",
                        )
                    )
                )
            )
        }
    }


    fun getAttractionsForDayPlan(groupId: Long, dayPlanId: Long): Flow<Resource<List<Attraction>>> {
        return flow {
            emit(
                Resource.Success(
                    listOf(
                        Attraction(
                            1,
                            0,
                            0,
                            "Sagrada Família",
                            "C/ de Mallorca, 401, 08013 Barcelona, Spain",
                            "An absolute must in Barcelona!",
                            "https://lh3.googleusercontent.com/p/AF1QipOicANzm_sbK0jBX4WnRf-U6UUb_MCfcRzdQbY-=s0",
                            "https://goo.gl/maps/LRFgZ2EeA2a2Hq5R7",
                            1.4
                        ),
                        Attraction(
                            2,
                            0,
                            0,
                            "Casa Milà",
                            "Passeig de Gràcia, 92, 08008 Barcelona, Spain",
                            "Great architecture!",
                            "https://lh3.googleusercontent.com/p/AF1QipM9Sp7ANPwGhzTLiVc-GYsPuE1RMMHoSBu_dzon=s0",
                            "https://g.page/casamila?share",
                            1.2
                        ),
                        Attraction(
                            3,
                            0,
                            0,
                            "Plaça de Catalunya",
                            "Plaça de Catalunya, 08002 Barcelona, Spain",
                            null,
                            "https://lh3.googleusercontent.com/p/AF1QipP64UqkKuxajFIBEX6n301zcY2nCdmhKxRCoNBu=s0",
                            "https://goo.gl/maps/7KefsXCfSX8WSYpd6",
                            1.1
                        ),
                        Attraction(
                            4,
                            0,
                            0,
                            "Colom Restaurant",
                            "Carrer dels Escudellers, 33, 08002 Barcelona, Spain",
                            "This restaurant offers seafood and traditional catalonian cousine. I've always wanted to try it.",
                            "https://lh3.googleusercontent.com/p/AF1QipM3Ov6A1ZqGDWp5HyLjjl_ey4nNntQiM9-0FxjM=s0",
                            "https://goo.gl/maps/fgGN9JNQ7eCbyo3L6",
                            2.1
                        ),
                        Attraction(
                            5,
                            0,
                            0,
                            "Barceloneta Beach",
                            "Pg. Marítim de la Barceloneta, S/N, 08003 Barcelona, Spain",
                            null,
                            "https://lh3.googleusercontent.com/p/AF1QipMAoptdcBZ-Nb0Gl-_Pgjn98U64_HwH_IPEG4Sz=s0",
                            "https://goo.gl/maps/dqDgBXcAxn4VQodL7",
                            null
                        )
                    )
                )
            )
        }
    }

}