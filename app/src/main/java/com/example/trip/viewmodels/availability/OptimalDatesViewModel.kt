package com.example.trip.viewmodels.availability

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.usecases.availability.GetOptimalDatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptimalDatesViewModel @Inject constructor(private val getOptimalDatesUseCase: GetOptimalDatesUseCase): ViewModel() {

    private val _availability by lazy {
        val mutableLiveData = MutableLiveData<Resource<Pair<Availability, Int>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val availability: LiveData<Resource<Pair<Availability, Int>>> = _availability

    fun refreshData() {
        getData(_availability)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<Pair<Availability, Int>>>) {
        viewModelScope.launch {
            getOptimalDatesUseCase(1).collect {
                mutableLiveData.value = it
            }
        }
    }

}