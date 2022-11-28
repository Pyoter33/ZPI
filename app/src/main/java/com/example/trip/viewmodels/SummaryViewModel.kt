package com.example.trip.viewmodels

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.*
import com.example.trip.usecases.group.GetGroupUseCase
import com.example.trip.usecases.participants.GetParticipantsUseCase
import com.example.trip.usecases.summary.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getAcceptedAccommodationUseCase: GetAcceptedAccommodationUseCase,
    private val getAcceptedAvailabilityUseCase: GetAcceptedAvailabilityUseCase,
    private val getParticipantsUseCase: GetParticipantsUseCase,
    private val deleteAcceptedAccommodationUseCase: DeleteAcceptedAccommodationUseCase,
    private val deleteAcceptedAvailabilityUseCase: DeleteAcceptedAvailabilityUseCase,
    private val postAcceptedAvailabilityUseCase: PostAcceptedAvailabilityUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val changeGroupStatusUseCase: ChangeGroupStageUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private var isAccommodationAdded = false
    private var isDateAdded = false

    private val _acceptedAccommodation = groupId?.let { getAcceptedAccommodationUseCase(it).asLiveData() }?: MutableLiveData()
    val acceptedAccommodation: LiveData<Resource<Accommodation?>> = _acceptedAccommodation

    private val _acceptedAvailability = groupId?.let { getAcceptedAvailabilityUseCase(it).asLiveData() }?: MutableLiveData()
    val acceptedAvailability: LiveData<Resource<OptimalAvailability?>> = _acceptedAvailability

    private val _participants = groupId?.let { getParticipantsUseCase(it).asLiveData() }?: MutableLiveData()
    val participants: LiveData<Resource<List<Participant>>> = _participants

    private val _isButtonUnlocked = MutableLiveData(false)
    val isButtonUnlocked: LiveData<Boolean> = _isButtonUnlocked

    fun changeGroupStatusAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                changeGroupStatusUseCase(it)
            }?: Resource.Failure()
        }
    }

    fun getGroupAsync(): Deferred<Resource<Group>> {
        return viewModelScope.async {
            groupId?.let {
                getGroupUseCase(it)
            }?: Resource.Failure()
        }
    }

    fun setNewAcceptedAvailabilityAsync(availability: Availability): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                postAcceptedAvailabilityUseCase(it, availability)
            }?: Resource.Failure()
        }
    }

    fun deleteAcceptedAvailabilityAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                deleteAcceptedAvailabilityUseCase(it)
            }?: Resource.Failure()
        }
    }

    fun deleteAcceptedAccommodationAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                deleteAcceptedAccommodationUseCase(it)
            }?: Resource.Failure()
        }
    }

    fun updateButtonLock(accommodationAdded: Boolean = isAccommodationAdded, dateAdded: Boolean = isDateAdded) {
        isAccommodationAdded = accommodationAdded
        isDateAdded = dateAdded
        _isButtonUnlocked.value = isAccommodationAdded && isDateAdded
    }

}