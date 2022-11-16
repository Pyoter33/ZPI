package com.example.trip.service

import com.example.trip.dto.TransportDto
import com.example.trip.models.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface TransportService {

    @GET("transport")
    suspend fun getTransport(
        @Query("accommodationId") accommodationId: Long
    ):Resource<List<TransportDto>>


    
}