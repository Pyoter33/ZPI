package com.example.trip.viewmodels.transport

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Resource
import com.example.trip.models.Transport
import com.example.trip.usecases.transport.GetMapsRouteUseCase
import com.example.trip.usecases.transport.GetTransportUseCase
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor(
    private val getTransportUseCase: GetTransportUseCase,
    private val getMapsRouteUseCase: GetMapsRouteUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val accommodationId = state.get<Long>(Constants.ACCOMMODATION_ID_KEY)
    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _transport by lazy {
        val mutableLiveData = MutableLiveData<Resource<Transport>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val transport: LiveData<Resource<Transport>> = _transport

    private val _route = MutableLiveData<Resource<PolylineOptions>?>()
    val route: LiveData<Resource<PolylineOptions>?> = _route

    fun refreshData() {
        getData(_transport)
    }

    fun getRoute(
        origin: String,
        destination: String
    ) {
        viewModelScope.launch {
            _route.value = getMapsRouteUseCase(origin, destination)
        }
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<Transport>>) {
        viewModelScope.launch {
                accommodationId?.let { accommodationId ->
                    groupId?.let { groupId ->
                        getTransportUseCase(accommodationId, groupId).collect {
                            mutableLiveData.value = it
                        }
                    }
            }
        }
    }

}