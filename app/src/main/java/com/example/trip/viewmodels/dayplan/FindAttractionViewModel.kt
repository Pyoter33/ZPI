package com.example.trip.viewmodels.dayplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trip.models.AttractionPreview
import com.example.trip.models.Resource
import com.example.trip.usecases.dayplan.GetAttractionPreviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindAttractionViewModel @Inject constructor(
    private val getAttractionPreviewUseCase: GetAttractionPreviewUseCase
) : ViewModel() {

    private val _attractionsList by lazy { MutableLiveData<Resource<List<AttractionPreview>>>() }
    val attractionsList: LiveData<Resource<List<AttractionPreview>>> = _attractionsList

    fun getData(query: String) {
        viewModelScope.launch {
            getAttractionPreviewUseCase(query).collect {
                _attractionsList.value = it
            }
        }
    }

}