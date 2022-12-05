package com.example.trip.viewmodels.transport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.dto.UserTransportPostDto
import com.example.trip.models.Resource
import com.example.trip.models.UserTransport
import com.example.trip.usecases.transport.PostTransportUseCase
import com.example.trip.usecases.transport.UpdateTransportUseCase
import com.example.trip.utils.toMinutesPartCompat
import com.example.trip.utils.toStringFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateEditTransportViewModel @Inject constructor(
    private val postTransportUseCase: PostTransportUseCase,
    private val updateTransportUseCase: UpdateTransportUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val accommodationId = state.get<Long>(Constants.ACCOMMODATION_ID_KEY)
    private val userTransportToUpdate = state.get<UserTransport>(Constants.USER_TRANSPORT_KEY)

    var toPost = true
    var meansOfTransport = userTransportToUpdate?.meansOfTransport?.toString()?.trim('[', ']')
    var meetingLocation = userTransportToUpdate?.source
    var destination = userTransportToUpdate?.destination
    var durationMinutes = userTransportToUpdate?.duration?.toMinutesPartCompat()?.toString()
    var durationHours = userTransportToUpdate?.duration?.toHours()?.toString()
    var meetingDate = userTransportToUpdate?.meetingDate
    var meetingTime = userTransportToUpdate?.meetingTime
    var price = userTransportToUpdate?.price?.toStringFormat()
    var description = userTransportToUpdate?.description


    fun postTransportAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
                accommodationId?.let { accommodationId ->
                    val transport = UserTransportPostDto(
                        Duration.ofHours(durationHours!!.toLong())
                            .plusMinutes(durationMinutes!!.toLong()),
                        BigDecimal.valueOf(price!!.toDouble()),
                        meetingLocation!!,
                        destination!!,
                        meetingDate!!,
                        meetingDate!!,
                        meansOfTransport!!,
                       description,
                        LocalDateTime.of(meetingDate!!, meetingTime!!),
                        ""
                    )
                    postTransportUseCase(accommodationId, transport)
                } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateTransportAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            userTransportToUpdate?.let {
                val transport = UserTransportPostDto(
                    Duration.ofHours(durationHours!!.toLong())
                        .plusMinutes(durationMinutes!!.toLong()),
                    BigDecimal.valueOf(price!!.toDouble()),
                    meetingLocation!!,
                    destination!!,
                    meetingDate!!,
                    meetingDate!!,
                    meansOfTransport!!,
                    description,
                    LocalDateTime.of(meetingDate!!, meetingTime!!),
                    ""
                )
                updateTransportUseCase(it.id, transport)
            } ?: Resource.Failure()
        }
        return deferred
    }

}