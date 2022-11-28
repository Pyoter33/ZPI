package com.example.trip.usecases.auth

import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.repositories.AuthRepository
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(loginRequestDto: LoginRequestDto): Resource<Pair<UserDto, String>> {
        return try {
            val result = authRepository.login(loginRequestDto)
            Resource.Success(result)
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Failure(e.code())
        } catch (e: ConnectException) {
            e.printStackTrace()
            Resource.Failure(0)
        }
    }

}