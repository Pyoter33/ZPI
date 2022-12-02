package com.example.trip.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.models.Resource
import com.example.trip.usecases.group.LeaveGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val leaveGroupUseCase: LeaveGroupUseCase,
    state: SavedStateHandle
) : ViewModel() {

    val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    fun leaveGroupAsync(): Deferred<Resource<Unit>> {
        return viewModelScope.async {
            groupId?.let {
                leaveGroupUseCase(it)
            } ?: Resource.Failure()
        }
    }

}