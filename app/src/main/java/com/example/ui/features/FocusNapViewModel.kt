package com.example.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FocusNapViewModel(
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _timeLeftMillis = MutableStateFlow(20L * 60 * 1000) // 20 mins
    val timeLeftMillis: StateFlow<Long> = _timeLeftMillis
    
    private var isRunning = false

    fun startNapSession() {
        if (isRunning) return
        isRunning = true
        
        // Schedule physical alarm
        val targetTime = System.currentTimeMillis() + _timeLeftMillis.value
        alarmScheduler.schedule(targetTime)
        
        viewModelScope.launch {
            while (_timeLeftMillis.value > 0 && isRunning) {
                delay(1000)
                _timeLeftMillis.value -= 1000
            }
        }
    }
    
    fun cancelNapSession() {
        isRunning = false
        alarmScheduler.cancel()
    }
}

class FocusNapViewModelFactory(
    private val alarmScheduler: AlarmScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusNapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusNapViewModel(alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
