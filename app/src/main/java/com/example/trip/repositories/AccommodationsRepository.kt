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

    fun getAcceptedAccommodation(groupId: Long): Flow<Resource<Accommodation?>> {
        return flow {
            emit(
                Resource.Success(
                    Accommodation(
                        1,
                        0,
                        1,
                        "Old Town Square Apartments",
                        "Masná 20, Praha, 110 00, Czechia",
                        "Nice apartment near the city centre.",
                        "https://cf.bstatic.com/xdata/images/hotel/max1280x900/242710752.jpg?k=11f43404aba1d4d2c69ec7d94bf08c2d4c12f20d11d01ef0ea7af0669383099a&o=&hp=1",
                        "https://www.booking.com/Share-8Hgjqk",
                        4,
                        BigDecimal(600),
                        true
                    )
                )
            )
        }
    }

    suspend fun deleteAcceptedAccommodation(accommodation: Accommodation): Resource<Unit> {
        return Resource.Failure()
    }

    fun getAccommodationsList(groupId: Long): Flow<Resource<List<Accommodation>>> {
        return flow {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    listOf(
                        Accommodation(
                            1,
                            0,
                            1,
                            "Old Town Square Apartments",
                            "Masná 20, Praha, 110 00, Czechia",
                            "Nice apartment near the city centre.",
                            "https://cf.bstatic.com/xdata/images/hotel/max1280x900/242710752.jpg?k=11f43404aba1d4d2c69ec7d94bf08c2d4c12f20d11d01ef0ea7af0669383099a&o=&hp=1",
                            "https://www.booking.com/Share-8Hgjqk",
                            4,
                            BigDecimal(600),
                            true
                        ),
                        Accommodation(
                            2,
                            0,
                            2,
                            "Durty Nelly's Pub",
                            "12 Melantrichova 3 patro, Praha, 110 00, Czechia",
                            "Cheaper than the other options but obviously worse standard. Near the metro.",
                            "https://cf.bstatic.com/xdata/images/hotel/max1024x768/123902669.jpg?k=c2261c9cee41a1b86a3dfc88a307f09c22b601e88b6b668f311f374ceb87ca31&o=&hp=1",
                            "https://www.booking.com/Share-6GIJVH",
                            1,
                            BigDecimal(200),
                            false
                        ),
                        Accommodation(
                            3,
                            0,
                            1,
                            "Hotel Libero",
                            "Plzeňská 577/25 577/25, Praha, 150 00, Czechia",
                            "Offers breakfast and is quite cheap.",
                            "https://cf.bstatic.com/xdata/images/hotel/max1280x900/306086215.jpg?k=927deb97459feafc50f4b43a1f488a0b240fbc7ecfec53eb7f717e5c639bd08d&o=&hp=1",
                            "https://www.booking.com/Share-8wDr29",
                            2,
                            BigDecimal(250),
                            false
                        ),
                        Accommodation(
                            4,
                            0,
                            3,
                            "Karl BY ZEITRAUM",
                            "2 Krakovská, Praha, 110 00, Czechia",
                            null,
                            "https://cf.bstatic.com/xdata/images/hotel/max1280x900/354470064.jpg?k=82a1f3c1f3e954716e845bf66432995f58ae9229106d81885500444a8bbbc259&o=&hp=1",
                            "https://www.booking.com/Share-eUufjx",
                            1,
                            BigDecimal(500),
                            true
                        )
                    )
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postAccommodation(groupId: Long, accommodationBase: Pair<String, String?>): Resource<Unit> {
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