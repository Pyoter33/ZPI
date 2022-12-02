package com.example.trip.usecases.participants

import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(userId: Long): Flow<Resource<AppUserDto>> {
        return flow {
            emit(Resource.Success(participantsRepository.getUser(userId)) as Resource<AppUserDto>)
        }.catch {
            it.printStackTrace()
            if (it.cause is HttpException) {
                emit(Resource.Failure((it.cause as HttpException).code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

}