package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Attraction
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
    private val attraction = state.get<Attraction>("attraction")

    fun postAttractionAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            attraction?.apply {
                description = descriptionText ?: ""
            }?.let {
                postAttractionUseCase(it)
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateAttractionAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            attraction?.apply {
                description = descriptionText ?: ""
            }?.let {
                updateAttractionUseCase(it)
            } ?: Resource.Failure()
        }
        return deferred
    }

}