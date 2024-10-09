package com.example.moneyeverydayxml.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.domain.Interactor

class ViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private var _operationsDate: MutableLiveData<Int> = MutableLiveData(0)
    val operationsDate: LiveData<Int> = _operationsDate



}