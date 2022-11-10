package com.example.trip.viewmodels.finances

import androidx.lifecycle.*
import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.usecases.finances.GetSettlementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettlementsViewModel @Inject constructor(private val getSettlementsUseCase: GetSettlementsUseCase, state: SavedStateHandle): ViewModel() {

    private val _settlementsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Settlement>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val settlementsList: LiveData<Resource<List<Settlement>>> = _settlementsList


    fun refreshData() {
        getData(_settlementsList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Settlement>>>) {
        viewModelScope.launch {
            getSettlementsUseCase(0).collect { //from args
                mutableLiveData.value = it
            }
        }
    }


}