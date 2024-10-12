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

    private val _sumAmount: MutableLiveData<String> =
        MutableLiveData(getSumFromMemory())
    val sumAmount: LiveData<String> = _sumAmount

    private val _daysFromClearPassedLiveData: MutableLiveData<String> =
        MutableLiveData("хуй")
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    private val _byDayAmount: MutableLiveData<String> =
        MutableLiveData("хуй")
    val byDay: LiveData<String> = _byDayAmount

    private var summaryAmount: Int = getSumFromMemory().toInt()
    private var daysFromClearPassed: Int = getDaysFromClear()
    private var summaryPerDayResult: Int = perDayCalculate()


    private val currentDate = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMM, EEEE, hh:mm", Locale.ENGLISH)
    private var dateOfClear: Long = interactor.getClearDate()

    private fun perDayCalculate(): Int {
        val r = summaryAmount / daysFromClearPassed
        _byDayAmount.postValue(r.toString())
        return r
    }

    private fun getSumFromMemory(): String = interactor.getSumFromMemory()
    fun getTodayDate(): String = formatter.format(currentDate)

    fun getDaysFromClear(): Int {
        val clearDateFromPrefs = dateOfClear
        if (clearDateFromPrefs == 0L) {
            _daysFromClearPassedLiveData.postValue("Сброса не было")
            return 1

        } else {
            daysFromClearPassed =
                (((currentDate - clearDateFromPrefs) / (1000 * 60 * 60 * 24)) + 1).toInt()
            _daysFromClearPassedLiveData.postValue(daysFromClearPassed.toString())
            return daysFromClearPassed
        }
    }

    fun decreaseAction(input: Int) {
        summaryAmount -= input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayDate(), summaryAmount)
    }

    fun increaseAction(input: Int) {
        summaryAmount += input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summaryAmount.toString())
        _byDayAmount.postValue(summaryPerDayResult.toString())
        interactor.saveData(input.toString(), getTodayDate(), summaryAmount)
    }

    fun clearAction() {
        summaryAmount = 0
        _sumAmount.postValue("0")
        dateOfClear = currentDate
        summaryPerDayResult = 0
        _byDayAmount.postValue("0")
        _daysFromClearPassedLiveData.postValue("1")
        interactor.saveClearDate(dateOfClear)
        interactor.saveData("Обнуление данных", getTodayDate(), summaryAmount)
    }

}