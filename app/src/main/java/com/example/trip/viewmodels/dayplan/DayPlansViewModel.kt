package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.DeleteDayPlanUseCase
import com.example.trip.usecases.dayplan.GetDayPlansUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DayPlansViewModel @Inject constructor(
    private val getDayPlansUseCase: GetDayPlansUseCase,
    private val deleteDayPlanUseCase: DeleteDayPlanUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _dayPlans by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<DayPlan>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val dayPlans: LiveData<Resource<List<DayPlan>>> = _dayPlans

    fun refresh() {
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

    fun deleteDayPlanAsync(dayPlanId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async(Dispatchers.IO) {
                deleteDayPlanUseCase(dayPlanId)
            }
    }
}