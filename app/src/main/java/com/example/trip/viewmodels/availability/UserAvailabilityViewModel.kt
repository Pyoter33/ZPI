package com.example.trip.viewmodels.availability

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.usecases.availability.GetUserAvailabilitiesUseCase
import com.example.trip.usecases.availability.PostAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAvailabilityViewModel @Inject constructor(private val getUserAvailabilitiesUseCase: GetUserAvailabilitiesUseCase, private val postAvailabilityUseCase: PostAvailabilityUseCase) :
    ViewModel() {

    private val _availabilityList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Availability>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val availabilityList: LiveData<Resource<List<Availability>>> = _availabilityList

    fun refreshData() {
        getData(_availabilityList)
    }

    suspend fun postAvailability(availability: Availability): Resource<Unit> {
        val job = viewModelScope.async(Dispatchers.IO) {
            postAvailabilityUseCase(availability)
        }
        return job.await()
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Availability>>>) {
        viewModelScope.launch {
            getUserAvailabilitiesUseCase(1).collect {
                mutableLiveData.value = it
            }
        }
    }
}
