package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.models.UserTransport
import com.example.trip.repositories.TransportRepository
import javax.inject.Inject

class PostTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(userTransport: UserTransport): Resource<Unit> {
        return transportRepository.postUserTransport(userTransport)
    }

}