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
    private var mainData = getMainData()
    private var summary = mainData.summaryAmount
    private var clearDate = mainData.dateOfClear
    private val currentDate = currentTimeInMillis()
    private val formatter = SimpleDateFormat("dd MMM, EEEE, HH:mm", Locale("ru"))

    private val _sumAmount: MutableLiveData<String> =
        MutableLiveData(summary.setScale(2, RoundingMode.DOWN).toString())
    private val _daysFromClearPassedLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _byDayAmount: MutableLiveData<String> = MutableLiveData("")
    private var summaryPerDayResult = perDayCalculate()

    val byDay: LiveData<String> = _byDayAmount
    val sumAmount: LiveData<String> = _sumAmount
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    private fun perDayCalculate(): BigDecimal {
        val r = summary / getDaysFromClear().toBigDecimal()
        _sumAmount.postValue(summary.setScale(2, RoundingMode.DOWN).toString())
        _byDayAmount.postValue(r.setScale(2, RoundingMode.DOWN).toString())
        return r
    }

    private fun getMainData(): MainData {
        return interactor.loadMainData()
    }

    fun refreshData() {
        mainData = interactor.loadMainData()
        _sumAmount.postValue(mainData.summaryAmount.setScale(2, RoundingMode.DOWN).toString())
    }

    private fun currentTimeInMillis(): Long = Calendar.getInstance().timeInMillis

    fun currentTimeFormattedString(): String {
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
        viewModelScope.launch {
            interactor.addTransactionAndUpdateSummary(
                Transaction(
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "- $input"
                )
            )
        }
    }

    fun increaseAction(input: BigDecimal) {
        summary += input
        summaryPerDayResult = perDayCalculate()
        viewModelScope.launch {
            interactor.addTransactionAndUpdateSummary(
                Transaction(
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "+ $input"
                )
            )
        }
    }

    fun clearAction() {
        summary = BigDecimal(0.0)
        perDayCalculate()
        clearDate = currentDate
        _daysFromClearPassedLiveData.postValue("День сброса сегодня")
        viewModelScope.launch {
            interactor.saveTransaction(
                Transaction(
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "Сброс статистики"
                )
            )
            interactor.saveMainData(MainData(clearDate, summary))
            interactor.clearAllTransactions()
        }
    }
}