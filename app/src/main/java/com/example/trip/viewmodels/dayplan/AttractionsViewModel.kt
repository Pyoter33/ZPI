package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Attraction
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.GetAttractionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val getAttractionsUseCase: GetAttractionsUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

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

    fun refreshData() {
        getData(_attractionsList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Attraction>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getAttractionsUseCase(1, groupId).collect { //from args
                    mutableLiveData.value = it
                }
            }
        }
    }

}