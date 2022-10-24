package com.example.trip.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.trip.models.Resource
import com.example.trip.models.Summary
import com.example.trip.usecases.summary.GetSummaryUseCase
import com.example.trip.usecases.summary.PostAcceptedAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val getSummaryUseCase: GetSummaryUseCase,
    private val postAcceptedAvailabilityUseCase: PostAcceptedAvailabilityUseCase,
    state: SavedStateHandle
): ViewModel() {

    private val groupId = state.get<Int>("groupId")!!

    private val _summary = getSummaryUseCase(groupId).asLiveData()
    val summary: LiveData<Resource<Summary>> = _summary

}