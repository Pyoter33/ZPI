package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.dto.AttractionCandidateDto
import com.example.trip.dto.AttractionDto
import com.example.trip.models.Attraction
import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.PostAttractionUseCase
import com.example.trip.usecases.dayplan.UpdateAttractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateEditAttractionViewModel @Inject constructor(
    private val postAttractionUseCase: PostAttractionUseCase,
    private val updateAttractionUseCase: UpdateAttractionUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var descriptionText: String? = null
    var toPost = false
    private val attraction = state.get<Attraction>(Constants.ATTRACTION_KEY)
    private val attractionPreview = state.get<AttractionPreview>(Constants.ATTRACTION_PREVIEW_KEY)
    private val dayPlanId = state.get<Long>(Constants.DAY_PLAN_ID_KEY)

    fun postAttractionAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            attractionPreview?.let {
                dayPlanId?.let { id ->
                    postAttractionUseCase(
                        id,
                        AttractionCandidateDto(
                            it.name,
                            it.address,
                            null,
                            it.latitude,
                            it.longitude,
                            it.placeId,
                            it.imageReference,
                            it.link,
                            descriptionText
                        )
                    )
                } ?: Resource.Failure()
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateAttractionAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            attraction?.let {
                updateAttractionUseCase(
                    AttractionDto(
                        it.id,
                        it.name,
                        descriptionText,
                        null,
                        it.address,
                        it.link,
                        it.imageReference,
                        0.0,
                        0.0
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}