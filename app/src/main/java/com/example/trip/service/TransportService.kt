package com.example.trip.service

import com.example.trip.dto.TransportDto
import com.example.trip.dto.UserTransportPostDto
import retrofit2.Response
import retrofit2.http.*

interface TransportService {

    @POST("transport")
    suspend fun getTransport(
        @Query("accommodationId") accommodationId: Long
    ): Response<List<TransportDto>>

    @POST("transport/user-transport")
    suspend fun postUserTransport(
        @Query("accommodationId") accommodationId: Long,
        @Body userTransportPostDto: UserTransportPostDto
    ): Response<Void>

    @PATCH("transport/user-transport")
    suspend fun updateUserTransport(
        @Query("transportId") transportId: Long,
        @Body userTransportPostDto: UserTransportPostDto
    ): Response<Void>

    @DELETE("transport/user-transport")
    suspend fun deleteUserTransport(
        @Query("accommodationId") accommodationId: Long,
        @Query("transportId") transportId: Long
    ): Response<Void>
    
}