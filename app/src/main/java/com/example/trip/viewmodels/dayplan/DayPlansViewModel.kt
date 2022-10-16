package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.GetDayPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayPlansViewModel @Inject constructor(private val getDayPlansUseCase: GetDayPlansUseCase): ViewModel() {

    private val _dayPlans by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<DayPlan>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val dayPlans: LiveData<Resource<List<DayPlan>>> = _dayPlans

    fun refreshData() {
        getData(_dayPlans)
    }

    suspend fun waitForDelay(): Resource<Unit> {
        val job = viewModelScope.async {
            delay(1000)
            Resource.Success(Unit)
        }
        return job.await()
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<DayPlan>>>) {
        viewModelScope.launch {
            getDayPlansUseCase(0).collect { //from args
                mutableLiveData.value = it
            }
        }
    }

}