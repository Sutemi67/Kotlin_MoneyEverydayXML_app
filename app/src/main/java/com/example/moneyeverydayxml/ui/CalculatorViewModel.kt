package com.example.moneyeverydayxml.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneyeverydayxml.domain.InteractorInterface
import java.math.BigDecimal
import java.math.RoundingMode
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
        MutableLiveData("")
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    private val _byDayAmount: MutableLiveData<String> =
        MutableLiveData("")
    val byDay: LiveData<String> = _byDayAmount

    private var summaryAmount: BigDecimal = getSumFromMemory().toBigDecimal()
    private var daysFromClearPassed: Long = getDaysFromClear()
    private var summaryPerDayResult: BigDecimal = perDayCalculate()

    private val currentDate = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMM, EEEE, hh:mm", Locale.ENGLISH)
    private var dateOfClear: Long = interactor.getClearDate()

    private fun perDayCalculate(): BigDecimal {
        val r = summaryAmount / daysFromClearPassed.toBigDecimal()
        _byDayAmount.postValue(r.setScale(2, RoundingMode.DOWN).toString())
        return r
    }

    private fun getSumFromMemory(): String = interactor.getSumFromMemory()
    fun getTodayDate(): String = formatter.format(currentDate)

    fun getDaysFromClear(): Long {
        val clearDateFromPrefs = dateOfClear
        if (clearDateFromPrefs == 0L) {
            _daysFromClearPassedLiveData.postValue("Сброса не было")
            return 1

        } else {
            daysFromClearPassed =
                ((currentDate - clearDateFromPrefs) / (1000 * 60 * 60 * 24)) + 1
            _daysFromClearPassedLiveData.postValue(daysFromClearPassed.toString())
            return daysFromClearPassed
        }
    }

    fun decreaseAction(input: BigDecimal) {
        summaryAmount -= input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summaryAmount.setScale(2, RoundingMode.DOWN).toString())
        _byDayAmount.postValue(summaryPerDayResult.setScale(2, RoundingMode.DOWN).toString())
        interactor.saveData("- $input", getTodayDate(), summaryAmount)
    }

    fun increaseAction(input: BigDecimal) {
        summaryAmount += input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summaryAmount.setScale(2, RoundingMode.DOWN).toString())
        _byDayAmount.postValue(summaryPerDayResult.setScale(2, RoundingMode.DOWN).toString())
        interactor.saveData("+ $input", getTodayDate(), summaryAmount)
    }

    fun clearAction() {
        summaryAmount = BigDecimal(0.0)
        _sumAmount.postValue("0")
        dateOfClear = currentDate
        summaryPerDayResult = BigDecimal(0.0)
        _byDayAmount.postValue("0")
        _daysFromClearPassedLiveData.postValue("1")
        interactor.saveClearDate(dateOfClear)
        interactor.saveData("Обнуление данных", getTodayDate(), summaryAmount)
    }

}