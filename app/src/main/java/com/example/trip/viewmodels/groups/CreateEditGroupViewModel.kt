package com.example.trip.viewmodels.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Group
import com.example.trip.models.GroupStatus
import com.example.trip.models.Resource
import com.example.trip.usecases.group.PostGroupUseCase
import com.example.trip.usecases.group.UpdateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateEditGroupViewModel @Inject constructor(
    private val postGroupUseCase: PostGroupUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var name: String? = null
    var startingCity: String? = null
    var currency: String? = null
    var participants: String? = null
    var days: String? = null
    var descriptionText: String? = null
    var toPost = false
    private val groupToUpdate = state.get<Group>("group")

    fun postGroupAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            postGroupUseCase(
                0,
                Group(
                    0,
                    name!!,
                    GroupStatus.PLANNING,
                    startingCity!!,
                    currency!!,
                    participants!!.toInt(),
                    days!!.toInt(),
                    descriptionText,
                    0
                )
            )
        }
        return deferred
    }

    fun updateGroupAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupToUpdate?.let {
                updateGroupUseCase(
                    Group(
                        it.id,
                        name!!,
                        it.groupStatus,
                        startingCity!!,
                        currency!!,
                        participants!!.toInt(),
                        days!!.toInt(),
                        descriptionText,
                        it.participantsNo
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}