package com.example.moneyeverydayxml.calculator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyeverydayxml.calculator.domain.InteractorInterface
import com.example.moneyeverydayxml.history.domain.model.MainData
import com.example.moneyeverydayxml.history.domain.model.Transaction
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculatorViewModel(
    private val interactor: InteractorInterface
) : ViewModel() {
    private val mainData = getMainData()
    private var summary = mainData.summaryAmount
    private var clearDate = mainData.dateOfClear
    private val currentDate = Calendar.getInstance().timeInMillis
    private val formatter = SimpleDateFormat("dd MMM, EEEE, HH:mm", Locale("ru"))

    private val _sumAmount: MutableLiveData<String> =
        MutableLiveData(summary.setScale(2, RoundingMode.DOWN).toString())
    private val _daysFromClearPassedLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _byDayAmount: MutableLiveData<String> = MutableLiveData("")
    val byDay: LiveData<String> = _byDayAmount
    val sumAmount: LiveData<String> = _sumAmount
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    private var daysFromClearPassed = getDaysFromClear()
    private var summaryPerDayResult = perDayCalculate()

    private fun perDayCalculate(): BigDecimal {
        val r = summary / daysFromClearPassed.toBigDecimal()
        _byDayAmount.postValue(r.setScale(2, RoundingMode.DOWN).toString())
        return r
    }

    private fun getMainData(): MainData {
        return interactor.loadMainData()
    }

    fun getTodayDate(): String {
        return formatter.format(currentDate)
    }

    fun getDaysFromClear(): Long {
        if (clearDate == 0L) {
            _daysFromClearPassedLiveData.postValue("Сброса не было")
            return 1

        } else {
            val daysFromClear =
                ((currentDate - clearDate) / (1000 * 60 * 60 * 24)) + 1
            _daysFromClearPassedLiveData.postValue(daysFromClear.toString())
            return daysFromClear
        }
    }

    fun decreaseAction(input: BigDecimal) {
        summary -= input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summary.setScale(2, RoundingMode.DOWN).toString())
        viewModelScope.launch {
            interactor.saveTransaction(
                Transaction(
                    date = getTodayDate().toString(),
                    count = input.toString()
                )
            )
            interactor.saveMainData(MainData(clearDate, summary))
        }
    }

    fun increaseAction(input: BigDecimal) {
        summary += input
        summaryPerDayResult = perDayCalculate()
        _sumAmount.postValue(summary.setScale(2, RoundingMode.DOWN).toString())
        viewModelScope.launch {
            interactor.saveTransaction(
                Transaction(
                    date = getTodayDate().toString(),
                    count = input.toString()
                )
            )
            interactor.saveMainData(MainData(clearDate, summary))

        }
    }

    fun clearAction() {
        summary = BigDecimal(0.0)
        _sumAmount.postValue("0")
        clearDate = currentDate
        summaryPerDayResult = BigDecimal(0.0)
        _byDayAmount.postValue("0")
        _daysFromClearPassedLiveData.postValue("1")
        interactor.saveMainData(MainData(clearDate, summary))
        viewModelScope.launch {
            interactor.saveTransaction(
                Transaction(
                    date = getTodayDate().toString(),
                    count = "Сброс"
                )
            )
            interactor.saveMainData(MainData(clearDate, summary))

        }
    }

}