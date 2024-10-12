package com.example.moneyeverydayxml.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.domain.InteractorInterface

class HistoryViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {

    private val _dates: MutableLiveData<List<String>> = MutableLiveData(interactor.getDatesList())
    val dates: LiveData<List<String>> = _dates

    private val _counts: MutableLiveData<List<String>> = MutableLiveData(interactor.getDatesList())
    val counts: LiveData<List<String>> = _counts

    fun loadDates() {
        _dates.postValue(interactor.getDatesList())
    }

    fun loadCounts() {
        _counts.postValue(interactor.getCountsList())
    }
    fun loadData(){
        interactor.loadData()
    }
}
