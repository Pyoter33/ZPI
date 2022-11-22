package com.example.trip.service

import com.example.trip.dto.AccommodationDto
import com.example.trip.dto.AccommodationPostDto
import com.example.trip.dto.AccommodationVoteDto
import com.example.trip.dto.AccommodationVotePostDto
import retrofit2.Response
import retrofit2.http.*

interface AccommodationService {

    @GET("accommodation")
    suspend fun getAccommodation(@Query("groupId") groupId: Long): Response<List<AccommodationDto>>

    @POST("accommodation")
    suspend fun postAccommodation(@Body accommodationPostDto: AccommodationPostDto): Response<Void>

    @PATCH("accommodation")
    suspend fun updateAccommodation(
        @Query("accommodationId") accommodationId: Long,
        @Body accommodationPostDto: AccommodationPostDto
    ): Response<Void>

    @DELETE("accommodation")
    suspend fun deleteAccommodation(@Query("accommodationId") accommodationId: Long): Response<Void>

    @GET("accommodation/vote")
    suspend fun getVotes(@Query("accommodationId") accommodationId: Long): Response<List<AccommodationVoteDto>>

    @POST("accommodation/votes")
    suspend fun postVote(@Query("accommodationVoteDto") accommodationVoteDto: AccommodationVotePostDto): Response<Void>

    //@GET accepted

}