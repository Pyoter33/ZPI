package com.example.trip.viewmodels.finances

import androidx.lifecycle.*
import com.example.trip.Constants
import com.example.trip.models.CheckableParticipant
import com.example.trip.models.Expense
import com.example.trip.models.Resource
import com.example.trip.usecases.finances.GetCheckableParticipantsUseCase
import com.example.trip.usecases.finances.PostExpenseUseCase
import com.example.trip.usecases.finances.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CreateEditExpenseViewModel @Inject constructor(
    private val getCheckableParticipantsUseCase: GetCheckableParticipantsUseCase,
    private val postExpenseUseCase: PostExpenseUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    state: SavedStateHandle
) : ViewModel() {

    var toPost = true
    var name: String? = null
    var price: String? = null

    val expense = state.get<Expense>(Constants.EXPENSE_KEY)
    val groupId = state.get<Long>(Constants.GROUP_ID_KEY)

    private val _participantsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<CheckableParticipant>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val participantsList: LiveData<Resource<List<CheckableParticipant>>> = _participantsList

    fun refreshData() {
        getData(_participantsList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<CheckableParticipant>>>) {
        viewModelScope.launch {
            groupId?.let {
                getCheckableParticipantsUseCase(it).collect {
                    mutableLiveData.value = it
                }
            }
        }
    }

    fun checkAll(isChecked: Boolean) {
        if (participantsList.value !is Resource.Success) return
        val currentPrice =  if(price.isNullOrEmpty()) BigDecimal.ZERO else price!!.toBigDecimal()
        val size = (participantsList.value as Resource.Success).data.size
        (participantsList.value as Resource.Success).data.forEach {
            it.isChecked = isChecked
            it.amount =
                if (isChecked) currentPrice.divide(BigDecimal.valueOf(size.toDouble()), 3, RoundingMode.DOWN) else BigDecimal.ZERO
        }
    }

    fun checkParticipant(position: Int, isChecked: Boolean): List<Int> {
        if (participantsList.value !is Resource.Success) return emptyList()
        (participantsList.value as Resource.Success).data[position].isChecked = isChecked
        return updateAmounts(position, isChecked)
    }

    private fun updateAmounts(
        position: Int,
        isChecked: Boolean
    ): List<Int> {
        val positionsToUpdate = mutableListOf<Int>()
        val currentPrice =  if(price.isNullOrEmpty()) BigDecimal.ZERO else price!!.toBigDecimal()
        if (!isChecked) {
            (participantsList.value as Resource.Success).data[position].amount = BigDecimal.ZERO
        }

        val checkedSize = (participantsList.value as Resource.Success).data.count { it.isChecked }

        (participantsList.value as Resource.Success).data.forEachIndexed { index, it ->
            if (it.isChecked) {
                positionsToUpdate.add(index)
                it.amount = currentPrice.divide(BigDecimal.valueOf(checkedSize.toDouble()),3, RoundingMode.DOWN)
            }
        }

        return positionsToUpdate
    }

    fun postExpenseAsync(): Deferred<Resource<Unit>> {
        val userId = 1L
        val participants = (participantsList.value as Resource.Success).data
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
            postExpenseUseCase(
                Expense(
                    0,
                    groupId,
                    participants.find { it.participant.id == userId }!!.participant,
                    LocalDate.now(),
                    name!!,
                    BigDecimal.valueOf(price!!.toDouble()),
                    participants.filter { it.isChecked }.map { it.participant }
                )
            )
            }?: Resource.Failure()

        }

        return deferred
    }

    fun updateExpenseAsync(): Deferred<Resource<Unit>> {
        val userId = 1L
        val participants = (participantsList.value as Resource.Success).data
        val deferred = viewModelScope.async(Dispatchers.IO) {
            groupId?.let { groupId ->
                expense?.let { expense ->
                    updateExpenseUseCase(
                        Expense(
                            expense.groupId,
                            groupId,
                            expense.creator,
                            expense.creationDate,
                            name!!,
                            BigDecimal.valueOf(price!!.toDouble()),
                            participants.filter { it.isChecked }.map { it.participant }
                        )
                    )
                }?: Resource.Failure()
            }?: Resource.Failure()
        }
        return deferred
    }

    fun setCheckedParticipants(expense: Expense): List<Int> {
        val positionsToUpdate = mutableListOf<Int>()
        val participantsIds = expense.debtors.map { it.id }
        val amount = expense.price.divide(BigDecimal.valueOf(participantsIds.size.toDouble()), 3, RoundingMode.DOWN)
        (participantsList.value as Resource.Success).data.forEachIndexed { index, it ->
            if (it.participant.id in participantsIds) {
                it.isChecked = true
                it.amount = amount
                positionsToUpdate.add(index)
            }
        }
        return positionsToUpdate
    }

}