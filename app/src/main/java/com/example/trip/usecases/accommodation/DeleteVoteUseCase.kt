package com.example.trip.usecases.accommodation

import com.example.trip.dto.AccommodationVoteId
import com.example.trip.models.Resource
import com.example.trip.repositories.AccommodationsRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DeleteVoteUseCase @Inject constructor(private val accommodationsRepository: AccommodationsRepository) {

    suspend operator fun invoke(accommodationVoteId: AccommodationVoteId): Resource<Unit> {
        return try {
            accommodationsRepository.deleteVote(accommodationVoteId)
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