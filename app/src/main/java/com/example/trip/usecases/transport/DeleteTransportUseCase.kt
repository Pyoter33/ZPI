package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import javax.inject.Inject

class DeleteTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(id: Long): Resource<Unit> {
        return transportRepository.deleteUserTransport(id)
    }

}