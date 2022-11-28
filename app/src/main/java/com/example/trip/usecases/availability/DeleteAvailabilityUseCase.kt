package com.example.trip.usecases.availability

import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DeleteAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(id: Long, groupId: Long): Resource<Unit> {
        return try {
            availabilityRepository.deleteAvailability(id, groupId)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }
}