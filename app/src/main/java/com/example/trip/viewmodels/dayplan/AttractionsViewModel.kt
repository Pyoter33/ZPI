package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.ViewModel
import com.example.trip.usecases.dayplan.GetAttractionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(private val getAttractionsUseCase: GetAttractionsUseCase): ViewModel() {



}