package com.example.trip.viewmodels.participants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.GetAttractionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(private val getAttractionsUseCase: GetAttractionsUseCase): ViewModel() {

    private val _participantsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Attraction>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val participantsList: LiveData<Resource<List<Attraction>>> = _participantsList

    fun setExpanded(position: Int) {
        if (participantsList.value is Resource.Success) {
            val accommodation = (participantsList.value!! as Resource.Success).data[position]
            accommodation.isExpanded = !accommodation.isExpanded
        }
    }

    fun refreshData() {
        getData(_participantsList)
    }

    suspend fun waitForDelay(): Resource<Unit> {
        val job = viewModelScope.async {
            delay(1000)
            Resource.Success(Unit)
        }
        return job.await()
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Attraction>>>) {
        viewModelScope.launch {
            getAttractionsUseCase(1, 1).collect { //from args
                mutableLiveData.value = it
            }
        }
    }

}