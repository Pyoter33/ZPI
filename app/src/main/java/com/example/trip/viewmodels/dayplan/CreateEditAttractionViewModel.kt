package com.example.trip.viewmodels.dayplan

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.PostAttractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CreateEditAttractionViewModel @Inject constructor(
    private val postAttractionUseCase: PostAttractionUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var linkText: String? = null
    var descriptionText: String? = null
    private val groupId = state.get<Int>("groupId")
    private val dayPlanId = state.get<Int>("dayPlanId")

    fun postAttractionAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            Log.i("tak", "dayPlan $dayPlanId groupId $groupId")
            if(dayPlanId == null || groupId == null) {
                return@async Resource.Failure()
            }
            postAttractionUseCase(groupId, dayPlanId, Pair(linkText!!, descriptionText))
        }
        return deferred
    }

}