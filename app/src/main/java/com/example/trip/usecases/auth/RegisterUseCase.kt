package com.example.trip.usecases.auth

import com.example.trip.dto.RegisterRequestDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import com.example.trip.utils.getMessage
import retrofit2.HttpException

import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(registerRequestDto: RegisterRequestDto): Resource<Unit> {
        return try {
            authRepository.register(registerRequestDto)
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