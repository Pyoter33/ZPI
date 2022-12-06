package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.Constants
import com.example.trip.dto.DayPlanPostDto
import com.example.trip.models.DayPlan
import com.example.trip.models.OptimalAvailability
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.PostDayPlanUseCase
import com.example.trip.usecases.dayplan.UpdateDayPlanUseCase
import com.example.trip.usecases.summary.GetAcceptedAvailabilityUseCase
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
    private val getAcceptedAvailabilityUseCase: GetAcceptedAvailabilityUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var name: String? = null
    var date: LocalDate? = null
    var icon: Int = 0
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null

    val groupId = state.get<Long>(Constants.GROUP_ID_KEY)
    val dayPlan = state.get<DayPlan>(Constants.DAY_PLAN_KEY)
    var toPost = true

    fun postDayPlanAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                postDayPlanUseCase(DayPlanPostDto(groupId, date!!, name!!.trim(), icon))
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun updateDayPlanAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                dayPlan?.let { dayPlan ->
                    val newDayPlan = DayPlanPostDto(groupId, date!!, name!!.trim(), icon)
                    updateDayPlanUseCase(dayPlan.id, newDayPlan)
                } ?: Resource.Failure()
            } ?: Resource.Failure()
        }
        return deferred
    }

    fun getAcceptedAvailabilityAsync(): Deferred<Resource<OptimalAvailability?>> {
        return viewModelScope.async {
            groupId?.let {
                getAcceptedAvailabilityUseCase(groupId)
            }?: Resource.Failure()
        }
    }

}