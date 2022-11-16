package com.example.trip.service

import com.example.trip.dto.AttractionCandidateDto
import com.example.trip.dto.AttractionDto
import com.example.trip.dto.DayPlanDto
import com.example.trip.dto.DayPlanPostDto
import retrofit2.Response
import retrofit2.http.*

interface DayPlanService {

    @GET("day-plan")
    suspend fun getDayPlansForGroup(@Query("groupId") groupId: Long): Response<DayPlanDto>

    @POST("day-plan")
    suspend fun postDayPlan(@Body dayPlanPostDto: DayPlanPostDto): Response<DayPlanDto>

    @PATCH("day-plan")
    suspend fun updateDayPlan(
        @Query("dayPlanId") dayPlanId: Long,
        @Body dayPlanPostDto: DayPlanPostDto
    ): Response<DayPlanDto>

    @DELETE("day-plan")
    suspend fun deleteDayPlan(@Query("dayPlanId") dayPlanId: Long): Response<Unit>

    @GET("attraction")
    suspend fun getAttractions(
        @Query("groupId") groupId: Long,
        @Query("dayPlanId") dayPlanId: Long
    ): Response<AttractionDto>

    @POST("attraction")
    suspend fun postAttraction(
        @Query("dayPlanId") dayPlanId: Long,
        @Body attractionCandidateDto: AttractionCandidateDto
    ): Response<AttractionDto>

    @PATCH("attraction")
    suspend fun updateAttraction(
        @Body attractionCandidateDto: AttractionDto
    ): Response<AttractionDto>

    @DELETE("attraction")
    suspend fun deleteAttraction(
        @Query("attractionId") attractionId: Long,
        @Query("dayPlanId") dayPlanId: Long
    ): Response<Unit>

    @GET("attraction/find")
    suspend fun findAttractions(
        @Query("name") name: String
    ): Response<List<AttractionCandidateDto>>

    @GET("optimize/{dayPlanId}")
    suspend fun getOptimizedAttractions(
        @Path("dayPlanId") dayPlanId: Long
    ): Response<List<AttractionDto>>

}