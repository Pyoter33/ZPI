package com.example.trip.usecases.participants

import com.example.trip.models.Resource
import com.example.trip.repositories.ParticipantsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import javax.inject.Inject

class GetInviteLinkUseCase @Inject constructor(private val participantsRepository: ParticipantsRepository) {

    operator fun invoke(groupId: Long): Flow<Resource<String>> {
        return flow {
            emit(Resource.Success(participantsRepository.getInviteLink(groupId)) as Resource<String>)
        }.catch {
            it.printStackTrace()
            if (it is HttpException) {
                emit(Resource.Failure(it.code()))
            } else {
                emit(Resource.Failure(0))
            }
        }.onStart {
            emit(Resource.Loading())
        }
    }

}