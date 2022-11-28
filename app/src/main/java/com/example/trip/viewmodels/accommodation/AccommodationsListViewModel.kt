package com.example.trip.viewmodels.accommodation

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.dto.AccommodationVoteId
import com.example.trip.dto.AccommodationVotePostDto
import com.example.trip.models.Accommodation
import com.example.trip.models.Resource
import com.example.trip.usecases.accommodation.DeleteAccommodationUseCase
import com.example.trip.usecases.accommodation.DeleteVoteUseCase
import com.example.trip.usecases.accommodation.GetAccommodationsListUseCase
import com.example.trip.usecases.accommodation.PostVoteUseCase
import com.example.trip.usecases.summary.UpdateAcceptedAccommodationUseCase
import com.example.trip.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccommodationsListViewModel @Inject constructor(
    private val getAccommodationsListUseCase: GetAccommodationsListUseCase,
    private val deleteAccommodationUseCase: DeleteAccommodationUseCase,
    private val postVoteUseCase: PostVoteUseCase,
    private val deleteVoteUseCase: DeleteVoteUseCase,
    private val postAcceptedAccommodationUseCase: UpdateAcceptedAccommodationUseCase,
    private val preferencesHelper: SharedPreferencesHelper,
    state: SavedStateHandle
) :
    ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

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

    fun filterAccommodations(userId: Long): Resource<List<Accommodation>> {
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

    fun filterVotedAccommodations(userId: Long): Resource<List<Accommodation>> {
        return when (accommodationsList.value) {
            is Resource.Success -> {
                Resource.Success((accommodationsList.value!! as Resource.Success).data.filter {
                    it.creatorId == userId && it.isVoted
                })
            }
            else -> Resource.Failure()
        }
    }

    fun postVoteAsync(accommodationId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                postVoteUseCase(
                    AccommodationVotePostDto(
                        preferencesHelper.getUserId(),
                        accommodationId,
                        it
                    )
                )
            } ?: Resource.Failure()
        }
    }

    fun deleteVoteAsync(accommodationId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                deleteVoteUseCase(
                    AccommodationVoteId(
                        preferencesHelper.getUserId(),
                        accommodationId,
                    )
                )
            } ?: Resource.Failure()
        }
    }

    fun deleteAccommodationAsync(accommodationId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            deleteAccommodationUseCase(
                accommodationId
            )
        }
    }

    fun postAcceptedAccommodationAsync(accommodationId: Long): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            postAcceptedAccommodationUseCase(
                accommodationId
            )
        }
    }


    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Accommodation>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getAccommodationsListUseCase(groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }
}