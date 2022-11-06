package com.example.trip.viewmodels.transport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class CreateEditTransportViewModel @Inject constructor(
    private val postTransportUseCase: PostTransportUseCase,
    private val updateTransportUseCase: UpdateTransportUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>("groupId")
    private val accommodationId = state.get<Long>("accommodationId")
    private val userTransportToUpdate = state.get<UserTransport>("userTransport")

    var toPost = false
    var meansOfTransport = userTransportToUpdate?.meansOfTransport?.toString()?.trim('[', ']')
    var meetingLocation = userTransportToUpdate?.source
    var destination = userTransportToUpdate?.destination
    var durationMinutes = userTransportToUpdate?.duration?.toMinutesPart()?.toString()
    var durationHours = userTransportToUpdate?.duration?.toHoursPart()?.toString()
    var meetingDate = userTransportToUpdate?.meetingDate
    var meetingTime = userTransportToUpdate?.meetingTime
    var price = userTransportToUpdate?.price?.toPlainString()
    var description = userTransportToUpdate?.description


    fun postTransportAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {

            groupId?.let { groupId ->
                accommodationId?.let { accommodationId ->
                    val transport = UserTransport(
                        0,
                        groupId,
                        accommodationId,
                        meansOfTransport!!.split(','),
                        Duration.ofHours(durationHours!!.toLong())
                            .plusMinutes(durationMinutes!!.toLong()),
                        meetingDate!!,
                        meetingTime!!,
                        BigDecimal.valueOf(price!!.toDouble()),
                        meetingLocation!!,
                        destination!!,
                        description
                    )
                    postTransportUseCase(transport)
                } ?: Resource.Failure()
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateTransportAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            userTransportToUpdate?.let {
                updateTransportUseCase(
                    UserTransport(
                        it.id,
                        it.groupId,
                        it.accommodationId,
                        meansOfTransport!!.split(','),
                        Duration.ofHours(durationHours!!.toLong())
                            .plusMinutes(durationMinutes!!.toLong()),
                        meetingDate!!,
                        meetingTime!!,
                        BigDecimal.valueOf(price!!.toDouble()),
                        meetingLocation!!,
                        destination!!,
                        description
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

}