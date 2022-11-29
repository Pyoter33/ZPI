package com.example.trip.usecases.participants

import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import retrofit2.HttpException

import javax.inject.Inject

class PostCoordinatorUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(groupId: Long, id: Long): Resource<Unit> {
        return try {
            participantsRepository.postCoordinator(groupId, id)
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