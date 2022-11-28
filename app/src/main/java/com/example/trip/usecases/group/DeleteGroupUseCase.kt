package com.example.trip.usecases.group

import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class DeleteGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(groupId: Long): Resource<Unit> {
        return try {
            groupsRepository.deleteGroup(groupId)
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