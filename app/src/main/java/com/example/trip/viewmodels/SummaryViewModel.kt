package com.example.trip.viewmodels

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Availability
import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.models.Summary
import com.example.trip.usecases.group.GetGroupUseCase
import com.example.trip.usecases.summary.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val deleteAcceptedAccommodationUseCase: DeleteAcceptedAccommodationUseCase,
    private val deleteAcceptedAvailabilityUseCase: DeleteAcceptedAvailabilityUseCase,
    private val postAcceptedAvailabilityUseCase: PostAcceptedAvailabilityUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val getSummaryUseCase: GetSummaryUseCase,
    private val changeGroupStatusUseCase: ChangeGroupStageUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    var startDate: LocalDate? = null
    private var isAccommodationAdded = false
    private var isDateAdded = false

    private val _summary by lazy {
        val mutableLiveData = MutableLiveData<Resource<Summary>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val summary: LiveData<Resource<Summary>> = _summary

    private val _isButtonUnlocked = MutableLiveData(false)
    val isButtonUnlocked: LiveData<Boolean> = _isButtonUnlocked

    fun changeGroupStatusAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                changeGroupStatusUseCase(it)
            } ?: Resource.Failure()
        }
    }

    fun getGroupAsync(): Deferred<Resource<Group>> {
        return viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                getGroupUseCase(it)
            } ?: Resource.Failure()
        }
    }

    fun setNewAcceptedAvailabilityAsync(availability: Availability): Deferred<Resource<Unit>> {
        return viewModelScope.async(Dispatchers.IO) {
            groupId?.let {
                postAcceptedAvailabilityUseCase(it, availability)
            } ?: Resource.Failure()
        }
    }

    fun deleteAcceptedAvailabilityAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                deleteAcceptedAvailabilityUseCase(it)
            } ?: Resource.Failure()
        }
    }

    fun deleteAcceptedAccommodationAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                deleteAcceptedAccommodationUseCase(it)
            } ?: Resource.Failure()
        }
    }

    fun refresh() {
        getData(_summary)
    }

    fun updateButtonLock(
        accommodationAdded: Boolean = isAccommodationAdded,
        dateAdded: Boolean = isDateAdded
    ) {
        isAccommodationAdded = accommodationAdded
        isDateAdded = dateAdded
        _isButtonUnlocked.value = isAccommodationAdded && isDateAdded
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<Summary>>) {
        viewModelScope.launch {
            groupId?.let {
                getSummaryUseCase(it).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }
}