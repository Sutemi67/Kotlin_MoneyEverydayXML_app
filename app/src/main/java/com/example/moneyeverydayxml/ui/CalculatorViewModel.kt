package com.example.moneyeverydayxml.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.domain.InteractorInterface
import java.text.SimpleDateFormat
import java.util.Calendar

class CalculatorViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {

    private val _sumAmount: MutableLiveData<String> = MutableLiveData("")
    val sumAmount: LiveData<String> = _sumAmount
    private val _byDayAmount: MutableLiveData<String> = MutableLiveData("")
    val byDay: LiveData<String> = _byDayAmount
    private val _daysFromClearPassedLiveData: MutableLiveData<String> = MutableLiveData("")
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    private var summaryPerDayResult: Int = 0
    private var summaryAmount: Int = 0
    private var daysFromClearPassed: Int = 1
    private val currentDate = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMMM yyyy: HH mm")
    private var dateOfClear: Long = 0

    fun getDaysFromClear(): String {
        daysFromClearPassed = (((currentDate - dateOfClear) / (1000 * 60 * 60 * 24)) + 1).toInt()
        return daysFromClearPassed.toString()
    }

    fun decreaseAction(input: Int) {
        summaryAmount -= input
        summaryPerDayResult = summaryAmount / daysFromClearPassed
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayAndData(), dateOfClear)
    }

    fun increaseAction(input: Int) {
        summaryAmount += input
        summaryPerDayResult = summaryAmount / daysFromClearPassed
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayAndData(), dateOfClear)
    }

    fun clearAction() {
        summaryAmount = 0
        dateOfClear = currentDate
        summaryPerDayResult = 0
        _daysFromClearPassedLiveData.postValue(getDaysFromClear())
//        interactor.saveData(input.toString(), getTodayAndData(), dateOfClear)
    }

    fun getTodayAndData(): String {
        dateOfClear = interactor.getData()
        return formatter.format(currentDate)
    }
}