package com.example.moneyeverydayxml.patterns.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneyeverydayxml.core.domain.model.NotificationPattern
import com.example.moneyeverydayxml.history.data.NotificationPatternDao
import kotlinx.coroutines.launch

class PatternsViewModel(
    private val patternDao: NotificationPatternDao
) : ViewModel() {

    private val _patterns = MutableLiveData<List<NotificationPattern>>()
    val patterns: LiveData<List<NotificationPattern>> = _patterns

    init {
        loadPatterns()
    }

    private fun loadPatterns() {
        viewModelScope.launch {
            patternDao.getAllPatterns().collect { patterns ->
                _patterns.value = patterns
            }
        }
    }

    fun addPattern(keywords: String, isIncome: Boolean) {
        viewModelScope.launch {
            val newPattern = NotificationPattern(
                keywords = keywords,
                isIncome = isIncome,
                isActive = true // Все шаблоны по умолчанию активны
            )
            patternDao.insertPattern(newPattern)
        }
    }

    fun deletePattern(pattern: NotificationPattern) {
        viewModelScope.launch {
            patternDao.deletePattern(pattern)
        }
    }
} 