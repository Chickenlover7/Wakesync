package com.example.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AlarmPreferences
import kotlinx.coroutines.launch

class SleepViewModel(
    private val alarmPreferences: AlarmPreferences
) : ViewModel() {

    fun startSleepSession() {
        viewModelScope.launch {
            alarmPreferences.setSleepStart(System.currentTimeMillis())
        }
    }
}

class SleepViewModelFactory(
    private val alarmPreferences: AlarmPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SleepViewModel(alarmPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
