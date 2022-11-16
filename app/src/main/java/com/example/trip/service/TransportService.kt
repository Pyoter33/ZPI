package com.example.trip.service

import com.example.trip.dto.TransportDto
import com.example.trip.dto.UserTransportDto
import com.example.trip.dto.UserTransportPostDto
import com.example.trip.models.Resource
import retrofit2.Response
import retrofit2.http.*

interface TransportService {

    @GET("transport")
    suspend fun getTransport(
        @Query("accommodationId") accommodationId: Long
    ):Resource<List<TransportDto>>

    @POST("transport/user-transport")
    suspend fun postUserTransport(
        @Query("accommodationId") accommodationId: Long,
        @Body userTransportPostDto: UserTransportPostDto
    ):Resource<UserTransportDto>

    @PATCH("transport/user-transport")
    suspend fun updateUserTransport(
        @Query("accommodationId") accommodationId: Long,
        @Query("transportId") transportId: Long,
        @Body userTransportPostDto: UserTransportPostDto
    ): Resource<UserTransportDto>

    @DELETE("transport/user-transport")
    suspend fun deleteUserTransport(
        @Query("accommodationId") accommodationId: Long,
        @Query("transportId") transportId: Long
    ): Response<Unit>
    
}