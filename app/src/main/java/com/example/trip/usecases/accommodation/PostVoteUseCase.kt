package com.example.trip.usecases.accommodation

import com.example.trip.dto.AccommodationVotePostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import retrofit2.HttpException

import javax.inject.Inject

class PostVoteUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodationVotePostDto: AccommodationVotePostDto): Resource<Unit> {
        return try {
            accommodationsRepository.postVote(accommodationVotePostDto)
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