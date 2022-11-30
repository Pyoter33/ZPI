package com.example.trip.service

import com.example.trip.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AccommodationService {

    @GET("accommodation/list")
    suspend fun getAccommodationsList(@Query("groupId") groupId: Long): Response<List<AccommodationDto>>

    @GET("accommodation")
    suspend fun getAccommodation(@Query("accommodationId") accommodationId: Long): Response<AccommodationDto>

    @POST("accommodation")
    suspend fun postAccommodation(@Body accommodationPostDto: AccommodationPostDto): Response<Void>

    @PATCH("accommodation")
    suspend fun updateAccommodation(
        @Query("accommodationId") accommodationId: Long,
        @Query("userId") userId: Long,
        @Body accommodationPostDto: AccommodationPostDto
    ): Response<Void>

    @DELETE("accommodation")
    suspend fun deleteAccommodation(@Query("accommodationId") accommodationId: Long): Response<Void>

    @GET("accommodation/vote")
    suspend fun getVotes(@Query("accommodationId") accommodationId: Long): Response<List<AccommodationVoteDto>>

    @POST("accommodation/vote")
    suspend fun postVote(@Body accommodationVoteDto: AccommodationVotePostDto): Response<Void>

    @HTTP(method = "DELETE", path = "accommodation/vote", hasBody = true)
    suspend fun deleteVote(@Body accommodationVoteId: AccommodationVoteId): Response<Void>

    @PATCH("accommodation/accept")
    suspend fun acceptAccommodation(@Query("accommodationId") accommodationId: Long): Response<Void>

}