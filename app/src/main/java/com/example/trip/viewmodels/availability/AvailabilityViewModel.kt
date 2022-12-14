package com.example.trip.viewmodels.availability

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.models.Availability
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.usecases.availability.DeleteAvailabilityUseCase
import com.example.trip.usecases.availability.GetOptimalDatesUseCase
import com.example.trip.usecases.availability.GetUserAvailabilitiesUseCase
import com.example.trip.usecases.availability.PostAvailabilityUseCase
import com.example.trip.usecases.summary.UpdateAcceptedAvailabilityUseCase
import com.example.trip.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val getUserAvailabilitiesUseCase: GetUserAvailabilitiesUseCase,
    private val postAvailabilityUseCase: PostAvailabilityUseCase,
    private val deleteAvailabilityUseCase: DeleteAvailabilityUseCase,
    private val getOptimalDatesUseCase: GetOptimalDatesUseCase,
    private val updateAcceptedAvailabilityUseCase: UpdateAcceptedAvailabilityUseCase,
    private val preferencesHelper: SharedPreferencesHelper,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _availabilityList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Availability>>>()
        getDataAvailability(mutableLiveData)
        return@lazy mutableLiveData
    }
    val availabilityList: LiveData<Resource<List<Availability>>> = _availabilityList

    private val _optimalAvailability by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<OptimalAvailability>>>()
        getDataOptimalAvailability(mutableLiveData)
        return@lazy mutableLiveData
    }
    val optimalAvailability: LiveData<Resource<List<OptimalAvailability>>> = _optimalAvailability

    fun refreshAvailability() {
        getDataAvailability(_availabilityList)
    }

    fun refreshOptimalAvailability() {
        getDataOptimalAvailability(_optimalAvailability)
    }

    fun postAvailabilityAsync(startDate: LocalDate, endDate: LocalDate): Deferred<Resource<Unit>> {
        return viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                postAvailabilityUseCase(
                    AvailabilityPostDto(
                        preferencesHelper.getUserId(),
                        it,
                        startDate,
                        endDate
                    )
                )
            } ?: Resource.Failure()
        }
    }

    fun updateAcceptedAvailabilityAsync(id: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            updateAcceptedAvailabilityUseCase(id)
        }
    }

    fun deleteAvailabilityAsync(id: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                deleteAvailabilityUseCase(
                    id,
                    groupId
                )
            } ?: Resource.Failure()
        }
    }

    private fun getDataAvailability(mutableLiveData: MutableLiveData<Resource<List<Availability>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getUserAvailabilitiesUseCase(preferencesHelper.getUserId(), groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    private fun getDataOptimalAvailability(mutableLiveData: MutableLiveData<Resource<List<OptimalAvailability>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getOptimalDatesUseCase(groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }


}