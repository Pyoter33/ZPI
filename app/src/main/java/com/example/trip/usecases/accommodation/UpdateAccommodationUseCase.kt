package com.example.trip.usecases.accommodation

import com.example.trip.dto.AccommodationPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.utils.SharedPreferencesHelper
import retrofit2.HttpException

import javax.inject.Inject

class UpdateAccommodationUseCase @Inject constructor(
    private val accommodationsRepository: AccommodationsRepository,
    private val preferencesHelper: SharedPreferencesHelper
) {

    suspend operator fun invoke(
        accommodationId: Long,
        accommodationPostDto: AccommodationPostDto
    ): Resource<Unit> {
        return try {
            accommodationsRepository.updateAccommodation(accommodationId, preferencesHelper.getUserId(), accommodationPostDto)
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