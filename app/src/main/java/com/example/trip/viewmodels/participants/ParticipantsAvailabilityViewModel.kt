package com.example.trip.viewmodels.participants

import androidx.lifecycle.*
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.usecases.availability.GetUserAvailabilitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantsAvailabilityViewModel @Inject constructor(
    private val getUserAvailabilitiesUseCase: GetUserAvailabilitiesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _availabilityList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Availability>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val availabilityList: LiveData<Resource<List<Availability>>> = _availabilityList

    fun refreshData() {
        getData(_availabilityList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Availability>>>) {
        viewModelScope.launch {
            getUserAvailabilitiesUseCase(1, 1).collect {
                mutableLiveData.value = it
            }
        }
    }
}