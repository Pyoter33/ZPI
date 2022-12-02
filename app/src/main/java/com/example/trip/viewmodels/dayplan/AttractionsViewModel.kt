package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Attraction
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.DeleteAttractionUseCase
import com.example.trip.usecases.dayplan.GetAttractionsUseCase
import com.example.trip.usecases.dayplan.UpdateStartingPointUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val getAttractionsUseCase: GetAttractionsUseCase,
    private val deleteAttractionUseCase: DeleteAttractionUseCase,
    private val updateStartingPointUseCase: UpdateStartingPointUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val dayPlan = state.get<DayPlan>(Constants.DAY_PLAN_KEY)

    private val _attractionsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Attraction>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val attractionsList: LiveData<Resource<List<Attraction>>> = _attractionsList

    fun setExpanded(position: Int) {
        if (attractionsList.value is Resource.Success) {
            val accommodation = (attractionsList.value!! as Resource.Success).data[position]
            accommodation.isExpanded = !accommodation.isExpanded
        }
    }

    fun refresh() {
        getData(_attractionsList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Attraction>>>) {
        viewModelScope.launch {
            if (dayPlan != null) {
                getAttractionsUseCase(dayPlan.id).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    fun deleteAttractionAsync(attractionId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            dayPlan?.let {
                deleteAttractionUseCase(attractionId, it.id)
            } ?: Resource.Failure()
        }
    }

    fun updateStartingPointAsync(attractionId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            dayPlan?.let {
                updateStartingPointUseCase(it.id, attractionId)
            } ?: Resource.Failure()
        }
    }
}