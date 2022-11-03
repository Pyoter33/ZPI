package com.example.trip.viewmodels.transport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.models.UserTransport
import com.example.trip.usecases.transport.PostTransportUseCase
import com.example.trip.usecases.transport.UpdateTransportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class CreateEditTransportViewModel @Inject constructor(
    private val postTransportUseCase: PostTransportUseCase,
    private val updateTransportUseCase: UpdateTransportUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var meansOfTransport: String? = null
    var meetingLocation: String? = null
    var destination: String? = null
    var durationHours: String? = null
    var durationMinutes: String? = null
    var meetingDate: LocalDate? = null
    var meetingTime: LocalTime? = null
    var price: String? = null
    var description: String? = null
    var toPost = false
    private val groupId = state.get<Long>("groupId")
    private val accommodationId = state.get<Long>("accommodationId")
    private val userTransport = state.get<UserTransport>("userTransport")

    fun postTransportAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {

                groupId?.let { groupId ->
                    accommodationId?.let { accommodationId ->
                        val transport = UserTransport(
                            0,
                            groupId,
                            accommodationId,
                            meansOfTransport!!.split(','),
                            Duration.ofHours(durationHours!!.toLong()).plusMinutes(durationMinutes!!.toLong()),
                            meetingDate!!,
                            meetingTime!!,
                            BigDecimal.valueOf(price!!.toDouble()),
                            meetingLocation!!,
                            destination!!,
                            description
                        )
                        postTransportUseCase(transport)
                    }?: Resource.Failure()
                }?: Resource.Failure()
        }
        return deferred
    }

    fun updateGroupAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupToUpdate?.let {
                postGroupUseCase(
                    0,
                    Group(
                        it.id,
                        name!!,
                        it.groupStatus,
                        startingCity!!,
                        currency!!,
                        descriptionText,
                        it.participantsNo
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}