package com.example.trip.usecases.transport

import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import retrofit2.HttpException
import javax.inject.Inject

class DeleteTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(accommodationId: Long, transportId: Long): Resource<Unit> {
        return try {
            transportRepository.deleteUserTransport(accommodationId, transportId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}