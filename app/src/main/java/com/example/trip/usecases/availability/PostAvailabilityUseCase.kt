package com.example.trip.usecases.availability

import com.example.trip.dto.AvailabilityPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AvailabilityRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class PostAvailabilityUseCase @Inject constructor(private val availabilityRepository: AvailabilityRepository) {

    suspend operator fun invoke(availabilityPostDto: AvailabilityPostDto): Resource<Unit> {
        return try {
            availabilityRepository.postAvailability(availabilityPostDto)
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