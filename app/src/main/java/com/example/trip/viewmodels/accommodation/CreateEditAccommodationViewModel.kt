package com.example.trip.viewmodels.accommodation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.dto.AccommodationPostDto
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.usecases.accommodation.PostAccommodationUseCase
import com.example.trip.usecases.accommodation.UpdateAccommodationUseCase
import com.example.trip.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CreateEditAccommodationViewModel @Inject constructor(
    private val postAccommodationUseCase: PostAccommodationUseCase,
    private val updateAccommodationUseCase: UpdateAccommodationUseCase,
    private val preferencesHelper: SharedPreferencesHelper,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)
    private val accommodation = state.get<Accommodation>(Constants.ACCOMMODATION_KEY)
    var linkText: String? = null
    var price: String? = null
    var descriptionText: String? = null
    var toPost = true


    fun postAccommodationAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                postAccommodationUseCase(
                    AccommodationPostDto(
                        groupId, preferencesHelper.getUserId(), linkText!!, descriptionText,
                        BigDecimal.valueOf(price!!.toDouble())
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateAccommodationAsync():  Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                updateAccommodationUseCase(
                    accommodation!!.id,
                    AccommodationPostDto(
                        groupId, preferencesHelper.getUserId(), linkText!!, descriptionText,
                        BigDecimal.valueOf(price!!.toDouble())
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}