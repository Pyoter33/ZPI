package com.example.trip.repositories

import com.example.trip.Constants
import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.RegisterRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.service.AuthService
import com.example.trip.utils.toBodyResourceOrFailure
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService) {

    suspend fun login(loginRequestDto: LoginRequestDto): Resource<UserDto>  {
        val result = authService.postLogin(loginRequestDto)
        return result.toBodyResourceOrFailure()
    }

    suspend fun register(registerRequestDto: RegisterRequestDto): Resource<String>  {
        val result = authService.postRegister(registerRequestDto)
        val token = result.headers()[Constants.AUTHORIZATION_HEADER]

        return if(result.isSuccessful && token != null) Resource.Success(token) else Resource.Failure(result.code())
    }

}