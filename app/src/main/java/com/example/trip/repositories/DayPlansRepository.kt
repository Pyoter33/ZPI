package com.example.trip.repositories

import com.example.trip.dto.*
import com.example.trip.service.DayPlanService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import javax.inject.Inject

class DayPlansRepository @Inject constructor(private val dayPlanService: DayPlanService) {

    suspend fun getDayPlans(groupId: Long): List<DayPlanDto> {
        return dayPlanService.getDayPlansForGroup(groupId).toBodyOrError()
    }

    suspend fun postDayPlan(dayPlanPostDto: DayPlanPostDto) {
        dayPlanService.postDayPlan(dayPlanPostDto).toNullableBodyOrError()
    }

    suspend fun updateDayPlan(dayPlanId: Long, dayPlanPostDto: DayPlanPostDto) {
        dayPlanService.updateDayPlan(dayPlanId, dayPlanPostDto).toNullableBodyOrError()
    }

    suspend fun deleteDayPlan(dayPlanId: Long) {
        dayPlanService.deleteDayPlan(dayPlanId).toNullableBodyOrError()
    }

    suspend fun getAttractionsForDayPlan(dayPlanId: Long): List<AttractionPlanDto> {
        return dayPlanService.getOptimizedAttractions(dayPlanId).toBodyOrError()
    }

    suspend fun postAttraction(dayPlanId: Long, attractionCandidateDto: AttractionCandidateDto) {
        dayPlanService.postAttraction(dayPlanId, attractionCandidateDto).toNullableBodyOrError()
    }

    suspend fun updateAttraction(attractionDto: AttractionDto) {
        dayPlanService.updateAttraction(attractionDto).toNullableBodyOrError()
    }

    suspend fun deleteAttraction(id: Long, dayPlanId: Long) {
        dayPlanService.deleteAttraction(id, dayPlanId).toNullableBodyOrError()
    }

    suspend fun getAttractionsForQuery(query: String): List<AttractionCandidateDto> {
        return dayPlanService.findAttractions(query).toBodyOrError()
    }

    //choose first attraction

}