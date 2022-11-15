package com.example.trip.service

import com.example.trip.dto.AccommodationDto
import com.example.trip.dto.AccommodationPostDto
import retrofit2.Response
import retrofit2.http.*

interface AccommodationService {

    @GET("accommodation")
    suspend fun getAccommodation(@Query("groupId") groupId: Long): Response<List<AccommodationDto>>

    @POST("accommodation")
    suspend fun postAccommodation(@Body accommodationPostDto: AccommodationPostDto): Response<AccommodationDto>

    @PATCH("accommodation")
    suspend fun updateAccommodation(
        @Query("accommodationId") accommodationId: Long,
        @Query("userId") userId: Long,
        @Body accommodationPostDto: AccommodationPostDto
    ): Response<AccommodationDto>

    @DELETE("accommodation")
    suspend fun deleteAccommodation(@Query("accommodationId") accommodationId: Long): Response<Unit>
}