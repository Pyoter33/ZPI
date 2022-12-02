package com.example.trip.service

import com.example.trip.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DayPlanService {

    @GET("day-plan")
    suspend fun getDayPlansForGroup(@Query("groupId") groupId: Long): Response<List<DayPlanDto>>

    @POST("day-plan")
    suspend fun postDayPlan(@Body dayPlanPostDto: DayPlanPostDto): Response<Void>

    @PATCH("day-plan")
    suspend fun updateDayPlan(
        @Query("dayPlanId") dayPlanId: Long,
        @Body dayPlanPostDto: DayPlanPostDto
    ): Response<Void>

    @DELETE("day-plan")
    suspend fun deleteDayPlan(@Query("dayPlanId") dayPlanId: Long): Response<Void>

    @POST("attraction")
    suspend fun postAttraction(
        @Query("dayPlanId") dayPlanId: Long,
        @Body attractionCandidateDto: AttractionCandidateDto
    ): Response<Void>

    @PATCH("attraction")
    suspend fun updateAttraction(
        @Body attractionDto: AttractionDto
    ): Response<Void>

    @DELETE("attraction")
    suspend fun deleteAttraction(
        @Query("attractionId") attractionId: Long,
        @Query("dayPlanId") dayPlanId: Long
    ): Response<Void>

    @GET("attraction/find")
    suspend fun findAttractions(
        @Query("name") name: String
    ): Response<List<AttractionCandidateDto>>

    @GET("attraction/optimize/{dayPlanId}")
    suspend fun getOptimizedAttractions(
        @Path("dayPlanId") dayPlanId: Long
    ): Response<List<AttractionPlanDto>>

    @PATCH("day-plan/start")
    suspend fun setStartingPoint(
        @Query("dayPlanId") dayPlanId: Long,
        @Query("attractionId") attractionId: Long
    ): Response<Void>

}