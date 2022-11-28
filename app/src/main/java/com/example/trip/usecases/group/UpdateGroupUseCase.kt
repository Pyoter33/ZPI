package com.example.trip.usecases.group

import com.example.trip.dto.TripGroupPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(groupId: Long, tripGroupPostDto: TripGroupPostDto): Resource<Unit> {
        return try {
            groupsRepository.updateGroup(groupId, tripGroupPostDto)
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