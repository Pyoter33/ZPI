package com.example.trip.usecases.dayplan

import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.repositories.DayPlansRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttractionPreviewUseCase @Inject constructor(private val dayPlansRepository: DayPlansRepository) {

    operator fun invoke(query: String): Flow<Resource<List<AttractionPreview>>> {
        return dayPlansRepository.getAttractionsForQuery(query)
    }

}