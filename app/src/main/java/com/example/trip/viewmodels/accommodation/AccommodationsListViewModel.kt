package com.example.trip.viewmodels.accommodation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.usecases.accommodation.GetAccommodationsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccommodationsListViewModel @Inject constructor(private val getAccommodationsListUseCase: GetAccommodationsListUseCase) :
    ViewModel() {

    private val _accommodationsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Accommodation>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val accommodationsList: LiveData<Resource<List<Accommodation>>> = _accommodationsList

    fun setVoted(position: Int) {
        if (accommodationsList.value is Resource.Success) {
            val accommodation = (accommodationsList.value!! as Resource.Success).data[position]
            accommodation.isVoted = !accommodation.isVoted
            accommodation.isVoted.let {
                if (it) accommodation.votes += 1 else accommodation.votes -= 1
            }
        }
    }

    fun setExpanded(position: Int) {
        if (accommodationsList.value is Resource.Success) {
            val accommodation = (accommodationsList.value!! as Resource.Success).data[position]
            accommodation.isExpanded = !accommodation.isExpanded
        }
    }

    fun refreshData() {
        getData(_accommodationsList)
    }

    fun resetFilter(): Resource<List<Accommodation>> {
        return when (accommodationsList.value) {
            is Resource.Success -> Resource.Success((accommodationsList.value!! as Resource.Success).data)
            else -> Resource.Failure()
        }
    }

    fun filterAccommodations(userId: Int): Resource<List<Accommodation>> {
        return when (accommodationsList.value) {
            is Resource.Success -> {
                Resource.Success((accommodationsList.value!! as Resource.Success).data.filter {
                    it.creatorId == userId
                })
            }
            else -> Resource.Failure()
        }
    }

    fun filterVoted(): Resource<List<Accommodation>> {
        return when (accommodationsList.value) {
            is Resource.Success -> {
                Resource.Success((accommodationsList.value!! as Resource.Success).data.filter {
                    it.isVoted
                })
            }
            else -> Resource.Failure()
        }
    }

    fun filterVotedAccommodations(userId: Int): Resource<List<Accommodation>> {
        return when (accommodationsList.value) {
            is Resource.Success -> {
                Resource.Success((accommodationsList.value!! as Resource.Success).data.filter {
                    it.creatorId == userId && it.isVoted
                })
            }
            else -> Resource.Failure()
        }
    }

    suspend fun waitForDelay(): Resource<Unit> {
        val job = viewModelScope.async {
            delay(1000)
            Resource.Success(Unit)
        }
        return job.await()
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Accommodation>>>) {
        viewModelScope.launch {
            getAccommodationsListUseCase().collect {
                mutableLiveData.value = it
            }
        }
    }
}