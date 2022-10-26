package com.example.trip.viewmodels

import androidx.lifecycle.*
import com.example.trip.models.Accommodation
import com.example.trip.models.Availability
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.usecases.participants.GetParticipantsUseCase
import com.example.trip.usecases.summary.GetAcceptedAccommodationUseCase
import com.example.trip.usecases.summary.GetAcceptedAvailabilityUseCase
import com.example.trip.usecases.summary.PostAcceptedAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getAcceptedAccommodationUseCase: GetAcceptedAccommodationUseCase,
    private val getAcceptedAvailabilityUseCase: GetAcceptedAvailabilityUseCase,
    private val getParticipantsUseCase: GetParticipantsUseCase,
    private val postAcceptedAvailabilityUseCase: PostAcceptedAvailabilityUseCase,
    state: SavedStateHandle
) : ViewModel() {

    //private val groupId = state.get<Int>("groupId")!!

    private var isAccommodationAdded = false
    private var isDateAdded = false

    private val _acceptedAccommodation = getAcceptedAccommodationUseCase(0).asLiveData()
    val acceptedAccommodation: LiveData<Resource<Accommodation?>> = _acceptedAccommodation

    private val _acceptedAvailability = getAcceptedAvailabilityUseCase(0).asLiveData()
    val acceptedAvailability: LiveData<Resource<Availability?>> = _acceptedAvailability

    private val _participants = getParticipantsUseCase(0).asLiveData()
    val participants: LiveData<Resource<List<Participant>>> = _participants

    private val _isButtonUnlocked = MutableLiveData(false)
    val isButtonUnlocked: LiveData<Boolean> = _isButtonUnlocked

    fun setNewAcceptedAvailabilityAsync(availability: Availability): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async {
            postAcceptedAvailabilityUseCase(availability)
        }
        return deferred
    }

    fun updateButtonLock(accommodationAdded: Boolean = isAccommodationAdded, dateAdded: Boolean = isDateAdded) {
        isAccommodationAdded = accommodationAdded
        isDateAdded = dateAdded
        _isButtonUnlocked.value = isAccommodationAdded && isDateAdded
    }

}