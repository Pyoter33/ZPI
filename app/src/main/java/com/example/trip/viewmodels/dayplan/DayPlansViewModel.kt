package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.GetDayPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayPlansViewModel @Inject constructor(
    private val getDayPlansUseCase: GetDayPlansUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _dayPlans by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<DayPlan>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val dayPlans: LiveData<Resource<List<DayPlan>>> = _dayPlans

    fun refreshData() {
        getData(_dayPlans)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<DayPlan>>>) {
        viewModelScope.launch {
            groupId?.let {
                getDayPlansUseCase(it).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

}