package com.example.trip.usecases.dayplan

import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import javax.inject.Inject

class DeleteAttractionUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {
    suspend operator fun invoke(id: Long, groupId: Long): Resource<Unit> {
        return dayPlansRepository.deleteAttraction(id, groupId)
    }

}