package com.example.trip.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.usecases.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase): ViewModel() {

    var email: String? = null
    var password: String? = null

    fun postLoginAsync(): Deferred<Resource<UserDto>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            loginUseCase(LoginRequestDto(email!!, password!!))
        }
        return deferred
    }


}