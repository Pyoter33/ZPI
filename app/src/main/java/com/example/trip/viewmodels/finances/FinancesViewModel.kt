package com.example.trip.viewmodels.finances

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.Balance
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.models.Settlement
import com.example.trip.usecases.finances.GetBalancesUseCase
import com.example.trip.usecases.finances.GetExpensesUseCase
import com.example.trip.usecases.finances.GetSettlementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinancesViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBalancesUseCase: GetBalancesUseCase,
    private val getSettlementsUseCase: GetSettlementsUseCase,
    state: SavedStateHandle
) : ViewModel() {

    private val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _expensesList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Expense>>>()
        getDataExpense(mutableLiveData)
        return@lazy mutableLiveData
    }
    val expensesList: LiveData<Resource<List<Expense>>> = _expensesList

    private val _balances by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Balance>>>()
        getDataBalances(mutableLiveData)
        return@lazy mutableLiveData
    }
    val balances: LiveData<Resource<List<Balance>>> = _balances

    private val _settlementsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Settlement>>>()
        getDataSettlements(mutableLiveData)
        return@lazy mutableLiveData
    }
    val settlementsList: LiveData<Resource<List<Settlement>>> = _settlementsList

    fun refreshDataExpense() {
        getDataExpense(_expensesList)
    }

    fun refreshDataBalances() {
        getDataBalances(_balances)
    }

    fun refreshDataSettlements() {
        getDataSettlements(_settlementsList)
    }

    fun resetFilter(): Resource<List<Expense>> {
        return when (val result = expensesList.value) {
            is Resource.Success -> result
            else -> Resource.Failure()
        }
    }

    fun filterMyExpenses(userId: Long): Resource<List<Expense>> {
        return when (val result = expensesList.value) {
            is Resource.Success -> {
                Resource.Success(result.data.filter {
                    it.creator.id == userId
                })
            }
            else -> Resource.Failure()
        }
    }

    fun filterContributions(userId: Long): Resource<List<Expense>> {
        return when (val result = expensesList.value) {
            is Resource.Success -> {
                Resource.Success(result.data.filter {
                    val participant = it.debtors.find { participant ->
                        participant.id == userId
                    }
                    participant?.let {
                        true
                    }?: false
                })
            }
            else -> Resource.Failure()
        }
    }

    fun filterMyExpensesContributions(userId: Long): Resource<List<Expense>> {
        return when (val result = expensesList.value) {
            is Resource.Success -> {
                Resource.Success(result.data.filter {
                    val filteredParticipant = it.debtors.find { participant ->
                        participant.id == userId
                    }
                    filteredParticipant?.let { filteredParticipant ->
                        filteredParticipant.id == it.creator.id
                    }?: false
                })
            }
            else -> Resource.Failure()
        }
    }

    private fun getDataExpense(mutableLiveData: MutableLiveData<Resource<List<Expense>>>) {
        viewModelScope.launch {
            groupId?.let {
                getExpensesUseCase(it).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    private fun getDataBalances(mutableLiveData: MutableLiveData<Resource<List<Balance>>>) {
        viewModelScope.launch {
            if (groupId != null) {
                getBalancesUseCase(groupId).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    private fun getDataSettlements(mutableLiveData: MutableLiveData<Resource<List<Settlement>>>) {
        viewModelScope.launch {
            groupId?.let {
                getSettlementsUseCase(it).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }
}