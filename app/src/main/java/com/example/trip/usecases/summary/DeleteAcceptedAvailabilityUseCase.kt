package com.example.trip.usecases.summary

import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import retrofit2.HttpException

import javax.inject.Inject

class DeleteAcceptedAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(groupId: Long): Resource<Unit> {
        return try {
            availabilityRepository.deleteAcceptedAvailability(groupId)
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