package com.example.trip.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TripGroupService {

    @GET("invitation")
    suspend fun getInvitation(
        @Query("group") groupId: Long
    ): Response<String>


}