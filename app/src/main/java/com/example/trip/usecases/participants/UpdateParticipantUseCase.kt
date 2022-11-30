package com.example.trip.usecases.participants

import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import retrofit2.HttpException
import javax.inject.Inject

class UpdateParticipantUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(userDto: UserDto): Resource<Unit> {
        return try {
            participantsRepository.updateParticipant(userDto)
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