package com.example.trip.usecases.participants

import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    suspend operator fun invoke(userId: Long): Flow<Resource<AppUserDto>> {
        return flow {
            emit(Resource.Success(participantsRepository.getUser(userId)) as Resource<AppUserDto>)
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }.flowOn(Dispatchers.IO)
    }

}