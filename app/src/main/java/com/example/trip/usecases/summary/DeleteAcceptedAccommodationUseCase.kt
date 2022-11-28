package com.example.trip.usecases.summary

import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DeleteAcceptedAccommodationUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(groupId: Long): Resource<Unit> {
        return try {
            accommodationsRepository.deleteAcceptedAccommodation(groupId)
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