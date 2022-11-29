package com.example.trip.viewmodels.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.dto.LoginRequestDto
import com.example.trip.dto.RegisterRequestDto
import com.example.trip.dto.UserDto
import com.example.trip.models.Resource
import com.example.trip.usecases.auth.LoginUseCase
import com.example.trip.usecases.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val registerUseCase: RegisterUseCase, private val loginUseCase: LoginUseCase) :
    ViewModel() {

    var email: String? = null
    var firstName: String? = null
    var surname: String? = null
    var code: String? = null
    var phone: String? = null
    var birthday: LocalDate? = null
    var password: String? = null
    var repeatPassword: String? = null

    fun postRegisterAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            registerUseCase(
                RegisterRequestDto(
                    email!!,
                    "+$code $phone",
                    password!!,
                    firstName!!,
                    surname!!,
                    birthday!!
                )
            )
        }
        return deferred
    }

    fun postLoginAsync(): Deferred<Resource<Pair<UserDto, String>>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            loginUseCase(LoginRequestDto(email!!, password!!))
        }
        return deferred
    }

}