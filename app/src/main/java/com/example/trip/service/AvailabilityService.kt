package com.example.trip.service

import com.example.trip.dto.AvailabilityDto
import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.dto.SharedGroupAvailabilityDto
import retrofit2.Response
import retrofit2.http.*

interface AvailabilityService {

    @GET("availability/user")
    suspend fun getAvailabilitiesForUser(
        @Query("groupId") groupId: Long
    ): Response<List<AvailabilityDto>>

    @POST("availability/user")
    suspend fun postAvailability(
        @Body availabilityPostDto: AvailabilityPostDto
    ): Response<AvailabilityDto>

    @DELETE("availability/user")
    suspend fun deleteAvailability(
        @Query("groupId") groupId: Long
    ): Response<Unit>

    @GET("shared-availability")
    suspend fun getAvailabilitiesForGroup(@Query("groupId") groupId: Long): Response<List<SharedGroupAvailabilityDto>>

}