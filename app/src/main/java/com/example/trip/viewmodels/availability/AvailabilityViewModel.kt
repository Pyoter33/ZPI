package com.example.trip.viewmodels.availability

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.usecases.availability.GetOptimalDatesUseCase
import com.example.trip.usecases.availability.GetUserAvailabilitiesUseCase
import com.example.trip.usecases.availability.PostAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvailabilityViewModel @Inject constructor(
    private val getUserAvailabilitiesUseCase: GetUserAvailabilitiesUseCase,
    private val postAvailabilityUseCase: PostAvailabilityUseCase,
    private val getOptimalDatesUseCase: GetOptimalDatesUseCase,
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
        val mutableLiveData = MutableLiveData<Resource<List<Pair<Availability, Int>>>>()
        getDataOptimalAvailability(mutableLiveData)
        return@lazy mutableLiveData
    }
    val optimalAvailability: LiveData<Resource<List<Pair<Availability, Int>>>> = _optimalAvailability

    fun refreshAvailability() {
        getDataAvailability(_availabilityList)
    }

    fun refreshOptimalAvailability() {
        getDataOptimalAvailability(_optimalAvailability)
    }

    suspend fun postAvailability(availability: Availability): Resource<Unit> {
        val job = viewModelScope.async(Dispatchers.IO) {
            postAvailabilityUseCase(availability)
        }
        return job.await()
    }

    private fun getDataAvailability(mutableLiveData: MutableLiveData<Resource<List<Availability>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getUserAvailabilitiesUseCase(1, groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    private fun getDataOptimalAvailability(mutableLiveData: MutableLiveData<Resource<List<Pair<Availability, Int>>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getOptimalDatesUseCase(groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }


}