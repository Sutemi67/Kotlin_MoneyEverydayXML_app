package com.example.moneyeverydayxml.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.domain.InteractorInterface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculatorViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {

    private var summaryPerDayResult: Int = 0
    private var summaryAmount: Int = 0
    private var daysFromClearPassed: Int = 1
    private val currentDate = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMM yyyy, EE", Locale.ENGLISH)
    private var dateOfClear: Long = currentDate

    private val _sumAmount: MutableLiveData<String> = MutableLiveData("")
    val sumAmount: LiveData<String> = _sumAmount
    private val _byDayAmount: MutableLiveData<String> = MutableLiveData("")
    val byDay: LiveData<String> = _byDayAmount
    private val _daysFromClearPassedLiveData: MutableLiveData<String> =
        MutableLiveData(getDaysFromClear())
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData


    private fun getDaysFromClear(): String {
        val clearDateFromPrefs = interactor.getClearDate()
        if (clearDateFromPrefs == 0L) {
            return "Сброса не было"
        } else {
            daysFromClearPassed =
                (((currentDate - clearDateFromPrefs) / (1000 * 60 * 60 * 24)) + 1).toInt()
            return daysFromClearPassed.toString()
        }
    }

    fun decreaseAction(input: Int) {
        summaryAmount -= input
        summaryPerDayResult = summaryAmount / daysFromClearPassed
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayDate())
    }

    fun increaseAction(input: Int) {
        summaryAmount += input
        summaryPerDayResult = summaryAmount / daysFromClearPassed
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayDate())
    }

    fun clearAction() {
        summaryAmount = 0
        dateOfClear = currentDate
        summaryPerDayResult = 0
        _daysFromClearPassedLiveData.postValue("1")
        interactor.saveClearDate(dateOfClear)
    }

    fun getTodayDate(): String {
        return formatter.format(currentDate)
    }
}