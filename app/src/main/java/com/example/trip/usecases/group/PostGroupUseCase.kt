package com.example.trip.usecases.group

import com.example.trip.dto.TripGroupPostDto
import com.example.trip.models.Resource
import com.example.trip.repositories.GroupsRepository
import com.example.trip.utils.getMessage
import retrofit2.HttpException

import javax.inject.Inject

class PostGroupUseCase @Inject constructor(private val groupsRepository: GroupsRepository) {

    suspend operator fun invoke(tripGroupPostDto: TripGroupPostDto): Resource<Unit> {
        return try {
            groupsRepository.postGroup(tripGroupPostDto)
            Resource.Success(Unit)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code(), e.response()?.getMessage())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}