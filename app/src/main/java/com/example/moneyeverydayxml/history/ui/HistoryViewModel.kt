package com.example.moneyeverydayxml.history.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyeverydayxml.calculator.domain.InteractorInterface
import com.example.moneyeverydayxml.history.domain.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {

    private var _transactions: MutableLiveData<List<Transaction>> = MutableLiveData()
    val transactions: LiveData<List<Transaction>> = _transactions

    fun loadTransactions(){
        viewModelScope.launch(Dispatchers.IO) {
            _transactions.postValue(interactor.loadTransactions())
        }
    }
}
