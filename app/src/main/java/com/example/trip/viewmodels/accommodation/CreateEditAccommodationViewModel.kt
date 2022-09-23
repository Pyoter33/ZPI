package com.example.trip.viewmodels.accommodation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Resource
import com.example.trip.usecases.accommodation.PostAccommodationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateEditAccommodationViewModel @Inject constructor(private val postAccommodationUseCase: PostAccommodationUseCase): ViewModel() {

    var linkText: String? = null
    var descriptionText: String? = null

    suspend fun postAccommodation(): Resource<Unit> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            postAccommodationUseCase(Pair(linkText!!, descriptionText))
        }
        return deferred.await()
    }

}