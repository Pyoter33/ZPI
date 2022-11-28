package com.example.trip.service

import com.example.trip.dto.AvailabilityDto
import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.dto.SharedGroupAvailabilityDto
import retrofit2.Response
import retrofit2.http.*
import java.time.LocalDate

interface AvailabilityService {

    @GET("availability/user")
    suspend fun getAvailabilitiesForUser(
        @Query("userId") userId: Long,
        @Query("groupId") groupId: Long
    ): Response<List<AvailabilityDto>>

    @POST("availability/user")
    suspend fun postAvailability(
        @Body availabilityPostDto: AvailabilityPostDto
    ): Response<Void>

    @DELETE("availability/user")
    suspend fun deleteAvailability(
        @Query("availabilityId") availabilityId: Long,
        @Query("groupId") groupId: Long
    ): Response<Void>

    @GET("shared-availability/list")
    suspend fun getAvailabilitiesForGroup(@Query("groupId") groupId: Long): Response<List<SharedGroupAvailabilityDto>>

    @GET("shared-availability")
    suspend fun getAcceptedAvailability(@Query("sharedGroupAvailabilityId") sharedGroupAvailabilityId: Long): Response<SharedGroupAvailabilityDto>

    @POST("shared-availability")
    suspend fun postAcceptedAvailability(
        @Query("dateFrom") dateFrom: LocalDate,
        @Query("dateTo") dateTo: LocalDate,
        @Query("groupId") groupId: Long,
    ): Response<Void>

    @PUT("shared-availability/accept")
    suspend fun acceptAvailability(
        @Query("sharedGroupAvailabilityId") sharedGroupAvailabilityId: Long
    ): Response<Void>

}