package com.example.moneyeverydayxml.calculator.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyeverydayxml.core.domain.RepositoryInterface
import com.example.moneyeverydayxml.core.domain.model.MainData
import com.example.moneyeverydayxml.core.domain.model.Transaction
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculatorViewModel(
    private val repository: RepositoryInterface
) : ViewModel() {
    private lateinit var mainData: MainData
    private lateinit var summary: BigDecimal
    private var clearDate: Long = 0L

    private val currentDate = currentTimeInMillis()
    private val formatter = SimpleDateFormat("dd MMM, EEEE, HH:mm", Locale("ru"))

    private val _sumAmount: MutableLiveData<String> = MutableLiveData()
    private val _daysFromClearPassedLiveData: MutableLiveData<String> = MutableLiveData("")
    private val _byDayAmount: MutableLiveData<String> = MutableLiveData("")
    private lateinit var summaryPerDayResult: BigDecimal

    val byDay: LiveData<String> = _byDayAmount
    val sumAmount: LiveData<String> = _sumAmount
    val daysFromClearPassedLiveData: LiveData<String> = _daysFromClearPassedLiveData

    init {
        viewModelScope.launch {
            refreshData()
        }
    }

    private fun formatNumberWithCommas(number: BigDecimal): String {
        val numberFormatter = DecimalFormat("#,##0.00")
        return numberFormatter.format(number)
    }

    private fun perDayCalculate(): BigDecimal {
        val days = getDaysFromClear()
        val result = summary.divide(days.toBigDecimal(), 2, RoundingMode.DOWN)
        _sumAmount.postValue(formatNumberWithCommas(summary.setScale(2, RoundingMode.DOWN)))
        _byDayAmount.postValue(formatNumberWithCommas(result.setScale(2, RoundingMode.DOWN)))
        return result
    }

    suspend fun refreshData() {
        mainData = getMainData()
        summary = mainData.summaryAmount
        clearDate = mainData.dateOfClear
        summaryPerDayResult = perDayCalculate()
    }

    suspend fun getMainData(): MainData = repository.loadMainData()
    private fun currentTimeInMillis(): Long = Calendar.getInstance().timeInMillis
    fun currentTimeFormattedString(): String = formatter.format(currentDate)

    fun getDaysFromClear(): Long {
        if (clearDate == 0L) {
            _daysFromClearPassedLiveData.postValue("Сброса не было - день первый")
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
            repository.addTransactionAndUpdateSummary(
                Transaction(
                    id = null,
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "-$input",
                    description = "Ручной ввод"
                )
            )
        }
    }

    fun increaseAction(input: BigDecimal) {
        summary += input
        summaryPerDayResult = perDayCalculate()
        viewModelScope.launch {
            repository.addTransactionAndUpdateSummary(
                Transaction(
                    id = null,
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "+$input",
                    description = "Ручной ввод"
                )
            )
        }
    }

    fun clearAction() {
        summary = BigDecimal(0.0)
        clearDate = currentDate
        perDayCalculate()
        _daysFromClearPassedLiveData.postValue("День сброса сегодня")
        viewModelScope.launch {
            repository.clearAllTransactions()
            repository.saveMainData(MainData(dateOfClear = clearDate, summaryAmount = summary))
            repository.saveTransaction(
                Transaction(
                    id = null,
                    time = currentTimeInMillis(),
                    date = currentTimeFormattedString(),
                    count = "Сброс статистики",
                    description = "Произведен сброс статистики"
                )
            )
        }
    }
}