package com.example.trip.viewmodels.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.dto.AppUserDto
import com.example.trip.models.Resource
import com.example.trip.usecases.participants.GetUserUseCase
import com.example.trip.usecases.participants.UpdateParticipantUseCase
import com.example.trip.utils.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val updateParticipantUseCase: UpdateParticipantUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val preferencesHelper: SharedPreferencesHelper,
) : ViewModel() {

    private val _user by lazy {
        val mutableLiveData = MutableLiveData<Resource<AppUserDto>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val user: LiveData<Resource<AppUserDto>> = _user

    var appUserDto: AppUserDto? = null
    var firstName: String? = null
    var surname: String? = null
    var code: String? = null
    var phone: String? = null
    var birthday: LocalDate? = null

    fun refresh() {
        getData(_user)
    }

    fun updateUserAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            appUserDto?.let {
                updateParticipantUseCase(
                    AppUserDto(
                        preferencesHelper.getUserId(),
                        "+$code $phone",
                        it.email,
                        firstName!!.trim(),
                        surname!!.trim(),
                        birthday!!,
                        it.registrationDate
                    )
                )
            } ?: Resource.Failure()
        }
        return deferred
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<AppUserDto>>) {
        viewModelScope.launch {
            getUserUseCase(preferencesHelper.getUserId()).collect {
                mutableLiveData.value = it
            }
        }
    }

}