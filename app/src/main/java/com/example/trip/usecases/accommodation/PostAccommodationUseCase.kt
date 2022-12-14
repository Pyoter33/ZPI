package com.example.trip.usecases.accommodation

import com.example.trip.dto.AccommodationPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import com.example.trip.utils.getMessage
import retrofit2.HttpException
import javax.inject.Inject

class PostAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodationPostDto: AccommodationPostDto): Resource<Unit> {
        return try {
            accommodationsRepository.postAccommodation(accommodationPostDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code(), e.response()?.getMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}