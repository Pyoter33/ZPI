package com.example.trip.viewmodels.groups

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor() :
    ViewModel() {

    var fullName: String? = null
    var code: String? = null
    var phone: String? = null
    var birthday: LocalDate? = null

}