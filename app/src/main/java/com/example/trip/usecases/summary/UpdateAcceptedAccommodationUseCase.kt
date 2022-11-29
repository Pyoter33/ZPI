package com.example.trip.usecases.summary

import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import retrofit2.HttpException

import javax.inject.Inject

class UpdateAcceptedAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodationId: Long): Resource<Unit> {
        return try{
            accommodationsRepository.postAcceptedAccommodation(accommodationId)
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