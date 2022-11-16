package com.example.trip.usecases.login

import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(loginRequestDto: LoginRequestDto): Resource<UserDto> {
        return authRepository.login(loginRequestDto)
    }

}