package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.R
import com.example.trip.models.DayPlan
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.PostDayPlanUseCase
import com.example.trip.usecases.dayplan.UpdateDayPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateEditDayPlanViewModel @Inject constructor(
    private val postDayPlanUseCase: PostDayPlanUseCase,
    private val updateDayPlanUseCase: UpdateDayPlanUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var name: String? = null
    var date: LocalDate? = null
    var icon: Int = R.drawable.ic_baseline_church_24

    val groupId = state.get<Long>("groupId")
    val dayPlan = state.get<DayPlan>("dayPlan")
    var toPost = true

    fun postDayPlanAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                val dayPlan = DayPlan(0, groupId, name!!, date!!, 0, icon)
                postDayPlanUseCase(dayPlan)
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateDayPlanAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                dayPlan?.let { dayPlan ->
                    val newDayPlan = DayPlan(
                        dayPlan.id,
                        groupId,
                        name!!,
                        date!!,
                        dayPlan.attractionsNumber,
                        icon
                    )
                    updateDayPlanUseCase(newDayPlan)
                } ?: Resource.Failure()
            } ?: Resource.Failure()
        }
        return deferred
    }

}