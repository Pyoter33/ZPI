package com.example.trip.service

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
    suspend fun updateDayPlan(@Query("dayPlanId") dayPlanId: Long, @Body dayPlanPostDto: DayPlanPostDto): Response<DayPlanDto>

    @DELETE("day-plan")
    suspend fun deleteDayPlan(@Query("dayPlanId") dayPlanId: Long): Response<Unit>

}