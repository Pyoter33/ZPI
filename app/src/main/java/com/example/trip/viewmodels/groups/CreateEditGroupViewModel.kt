package com.example.trip.viewmodels.groups

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.dto.TripGroupPostDto
import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.usecases.group.PostGroupUseCase
import com.example.trip.usecases.group.UpdateGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.util.*
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
    var toPost = true
    private val groupToUpdate = state.get<Group>(Constants.GROUP_KEY)

    fun postGroupAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            postGroupUseCase(
                TripGroupPostDto(
                    name!!,
                    Currency.getInstance(currency!!),
                    descriptionText,
                   0,
                    startingCity!!,
                    startingCity!!,
                    days!!.toInt(),
                    participants!!.toInt()
                )
            )
        }
        return deferred
    }

    fun updateGroupAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupToUpdate?.let {
                updateGroupUseCase(
                    it.id,
                    TripGroupPostDto(
                        name!!,
                        Currency.getInstance(currency!!),
                        descriptionText,
                        0,
                        startingCity!!,
                        startingCity!!,
                        days!!.toInt(),
                        participants!!.toInt()
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}