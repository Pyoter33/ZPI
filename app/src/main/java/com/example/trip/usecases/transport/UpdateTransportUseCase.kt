package com.example.trip.usecases.transport

import com.example.trip.dto.UserTransportPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.TransportRepository
import retrofit2.HttpException
import javax.inject.Inject

class UpdateTransportUseCase @Inject constructor(private val transportRepository: TransportRepository) {

    suspend operator fun invoke(transportId: Long, userTransportPostDto: UserTransportPostDto): Resource<Unit> {
        return try {
            transportRepository.updateUserTransport(transportId, userTransportPostDto)
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