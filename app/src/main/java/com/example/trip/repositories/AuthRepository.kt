package com.example.trip.repositories

import com.example.trip.Constants
import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.RegisterRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.service.AuthService
import com.example.trip.utils.toBodyOrError
import com.example.trip.utils.toNullableBodyOrError
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService) {

    suspend fun login(loginRequestDto: LoginRequestDto): Pair<UserDto, String> {
        val result = authService.postLogin(loginRequestDto)
        val token = result.headers()[Constants.AUTHORIZATION_HEADER] ?: throw HttpException(result)
        return Pair(result.toBodyOrError(), token)
    }

    suspend fun register(registerRequestDto: RegisterRequestDto) {
       authService.postRegister(registerRequestDto).toNullableBodyOrError()
    }

}