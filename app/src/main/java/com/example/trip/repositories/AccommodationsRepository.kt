package com.example.trip.repositories

import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.math.BigDecimal
import javax.inject.Inject


class AccommodationsRepository @Inject constructor() {

    fun getAcceptedAccommodation(groupId: Int): Flow<Resource<Accommodation?>> {
        return flow {
            emit(
                Resource.Success(
                    Accommodation(
                        2,
                        0,
                        1,
                        "Marsala Flexyrent apartment exclusive",
                        "7 Via Marsala, Centrum Mediolanu, 20121 Mediolan, Włochy",
                        "Description",
                        "",
                        "id2",
                        4,
                        BigDecimal(1200),
                        true
                    )
                )
            )
        }
    }


    fun getAccommodationsList(groupId: Int): Flow<Resource<List<Accommodation>>> {
        return flow {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    listOf(
                        Accommodation(
                            1,
                            0,
                            1,
                            "Hotel 1",
                            "Address 1",
                            null,
                            "id1",
                            "",
                            2,
                            BigDecimal(200),
                            false
                        ),
                        Accommodation(
                            2,
                            0,
                            1,
                            "Marsala Flexyrent apartment exclusive",
                            "7 Via Marsala, Centrum Mediolanu, 20121 Mediolan, Włochy",
                            "Description",
                            "",
                            "id2",
                            4,
                            BigDecimal(1200),
                            true
                        ),
                        Accommodation(
                            3,
                            0,
                            2,
                            "Hotel 3",
                            "Address 3",
                            "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis,",
                            "",
                            "id3",
                            2,
                            BigDecimal(200),
                            false
                        ),
                        Accommodation(
                            4,
                            0,
                            3,
                            "Hotel 4",
                            "Address 4",
                            null,
                            "",
                            "id4",
                            1,
                            BigDecimal(500),
                            true
                        ),
                        Accommodation(
                            5,
                            0,
                            1,
                            "Hotel 5",
                            "Address 5",
                            "awegwegawegawe",
                            "id5",
                            "",
                            1,
                            BigDecimal(250),
                            false
                        )
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postAccommodation(groupId: Int, accommodationBase: Pair<String, String?>): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun postAcceptedAccommodation(
        accommodation: Accommodation
    ): Resource<Unit> {
        return Resource.Failure()
    }

    suspend fun updateAccommodation(
        accommodation: Accommodation
    ): Resource<Unit> {
        return Resource.Failure()
    }

}