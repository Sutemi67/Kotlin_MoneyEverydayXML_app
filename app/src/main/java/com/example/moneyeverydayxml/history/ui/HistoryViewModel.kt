package com.example.moneyeverydayxml.history.ui

import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.calculator.domain.InteractorInterface

class HistoryViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {

//    private val _dates: MutableLiveData<List<String>> = MutableLiveData(interactor.getDatesList())
//    val dates: LiveData<List<String>> = _dates
//
//    private val _counts: MutableLiveData<List<String>> = MutableLiveData(interactor.getDatesList())
//    val counts: LiveData<List<String>> = _counts

    fun loadData(){
        interactor.loadMainData()
    }
}
