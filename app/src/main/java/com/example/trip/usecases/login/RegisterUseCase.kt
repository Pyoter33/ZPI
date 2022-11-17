package com.example.trip.usecases.login

import com.example.trip.dto.RegisterRequestDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(registerRequestDto: RegisterRequestDto): Resource<String> {
        return authRepository.register(registerRequestDto)
    }

}