package com.example.trip.viewmodels.groups

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.Group
import com.example.trip.models.Resource
import com.example.trip.usecases.group.GetGroupsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsListViewModel @Inject constructor(private val getGroupsUseCase: GetGroupsUseCase): ViewModel() {

    private val _groupsList by lazy {
        val mutableLiveData = MutableLiveData<Resource<List<Group>>>()
        getData(mutableLiveData)
        return@lazy mutableLiveData
    }
    val groupsList: LiveData<Resource<List<Group>>> = _groupsList

    fun refreshData() {
        getData(_groupsList)
    }

    private fun getData(mutableLiveData: MutableLiveData<Resource<List<Group>>>) {
        viewModelScope.launch {
            getGroupsUseCase(1).collect { //from args
                mutableLiveData.value = it
            }
        }
    }

}