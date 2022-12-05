package com.example.trip.viewmodels.finances

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.CheckableParticipant
import com.example.trip.models.Expense
import com.example.trip.models.Participant
import com.example.trip.models.Resource
import com.example.trip.usecases.finances.DeleteExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class ExpenseDetailsViewModel @Inject constructor(
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    state: SavedStateHandle
) : ViewModel() {
    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)
    private val expense = state.get<Expense>(Constants.EXPENSE_KEY)

    private val _participants = MutableLiveData<List<CheckableParticipant>>()
    val participants: LiveData<List<CheckableParticipant>> = _participants

    fun setParticipants(participants: List<Participant>, amount: BigDecimal) {
        val size = participants.size.toDouble()
        _participants.value = participants.map {
            CheckableParticipant(
                it,
                amount.divide(BigDecimal.valueOf(size), 3, RoundingMode.DOWN),
                false
            )
        }
    }

    fun deleteExpenseAsync(): Deferred<Resource<Unit>> {
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                expense?.let {
                    deleteExpenseUseCase(
                        it.id, groupId
                    )
                }
            } ?: Resource.Failure()
        }
        return deferred
    }

}