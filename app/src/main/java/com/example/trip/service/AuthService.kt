package com.example.trip.service

import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.RegisterRequestDto
import com.example.trip.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    suspend fun postLogin(@Body loginRequestDto: LoginRequestDto): Response<UserDto>

    @POST("auth/register")
    suspend fun postRegister(@Body registerRequestDto: RegisterRequestDto): Response<Void>

}