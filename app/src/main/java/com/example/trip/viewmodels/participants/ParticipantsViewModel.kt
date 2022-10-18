package com.example.trip.viewmodels.participants

import androidx.lifecycle.*
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.usecases.participants.DeleteParticipantsUseCase
import com.example.trip.usecases.participants.GetInviteLinkUseCase
import com.example.trip.usecases.participants.GetParticipantsUseCase
import com.example.trip.usecases.participants.UpdateParticipantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val getParticipantsUseCase: GetParticipantsUseCase,
    private val getInviteLinkUseCase: GetInviteLinkUseCase,
    private val updateParticipantUseCase: UpdateParticipantUseCase,
    private val deleteParticipantsUseCase: DeleteParticipantsUseCase
) : ViewModel() {

    private val _participantsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Participant>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val participantsList: LiveData<Resource<List<Participant>>> = _participantsList

    private val _inviteLink by lazy {
        return@lazy getInviteLinkUseCase(0).asLiveData()
    }
    val inviteLink: LiveData<Resource<String>> = _inviteLink

    fun refreshData() {
        getData(_participantsList)
    }

    fun filterParticipants(query: String): List<Participant> {
        if(participantsList.value !is Resource.Success) {
            return listOf()
        }

        if(query.isEmpty()) {
            return (participantsList.value as Resource.Success).data
        }

        val filteredList = (participantsList.value as Resource.Success).data.filter {
            it.fullName.lowercase().contains(query.lowercase())
        }

        return filteredList
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Participant>>>) {
        viewModelScope.launch {
            getParticipantsUseCase(1).collect { //from args
                mutableLiveData.value = it
            }
        }
    }

}