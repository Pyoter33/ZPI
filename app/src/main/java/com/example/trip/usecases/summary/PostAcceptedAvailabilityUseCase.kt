package com.example.trip.usecases.summary

import com.example.trip.models.Availability
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import retrofit2.HttpException

import javax.inject.Inject

class PostAcceptedAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(groupId: Long, availability: Availability): Resource<Unit> {
        return try{
            availabilityRepository.postAcceptedAvailability(groupId, availability)
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