package com.example.trip.usecases.group

import com.example.trip.dto.TripGroupPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.getMessage
import retrofit2.HttpException
import java.io.EOFException

import javax.inject.Inject

class UpdateGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(groupId: Long, tripGroupPostDto: TripGroupPostDto): Resource<Unit> {
        return try {
            groupsRepository.updateGroup(groupId, tripGroupPostDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code(), e.response()?.getMessage())
        } catch (e: EOFException) {
            e.printStackTrace()
            Resource.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}