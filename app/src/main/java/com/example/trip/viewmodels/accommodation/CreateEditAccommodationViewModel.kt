package com.example.trip.viewmodels.accommodation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Resource
import com.example.trip.usecases.accommodation.PostAccommodationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateEditAccommodationViewModel @Inject constructor(
    private val postAccommodationUseCase: PostAccommodationUseCase,
    state: SavedStateHandle
) : ViewModel() {

    val groupId = state.get<Int>("groupId")
    var linkText: String? = null
    var descriptionText: String? = null

    suspend fun postAccommodation(): Resource<Unit> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                postAccommodationUseCase(groupId, Pair(linkText!!, descriptionText))
            }?: Resource.Failure()
        }
        return deferred.await()
    }

}