package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.models.Transport
import com.example.trip.repositories.TransportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    operator fun invoke(groupId: Long, accommodationId: Long): Flow<Resource<Transport>> {
        return transportRepository.getTransport(groupId, accommodationId)
    }

}