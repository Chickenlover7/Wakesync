package com.example.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AlarmPreferences
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WakeUiState(
    val tasks: List<Task> = emptyList(),
    val sleepDurationMillis: Long = 0L,
    val allTasksCompleted: Boolean = false,
    val currentStreak: Int = 0
)

class WakeViewModel(
    private val taskRepository: TaskRepository,
    private val alarmPreferences: AlarmPreferences
) : ViewModel() {
    
    private val wakeSessionStartTime = System.currentTimeMillis()
    private var streakIncrementedThisSession = false

    val uiState: StateFlow<WakeUiState> = combine(
        taskRepository.allTasks,
        alarmPreferences.sleepStartTime,
        alarmPreferences.currentStreak
    ) { tasks, sleepStart, streak ->
        val sleepDuration = if (sleepStart != null && sleepStart > 0) {
            wakeSessionStartTime - sleepStart
        } else {
            0L
        }
        
        val totalTasks = tasks.size
        val allCompleted = totalTasks > 0 && tasks.all { it.isCompleted }

        WakeUiState(
            tasks = tasks,
            sleepDurationMillis = sleepDuration,
            allTasksCompleted = allCompleted,
            currentStreak = streak
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WakeUiState()
    )

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val isNowCompleted = !task.isCompleted
            taskRepository.update(
                task.copy(
                    isCompleted = isNowCompleted,
                    completedAt = if (isNowCompleted) System.currentTimeMillis() else null
                )
            )
            
            // Streak logic: check if task is being completed within 30 mins of waking up
            if (isNowCompleted && !streakIncrementedThisSession) {
                val timeSinceWake = System.currentTimeMillis() - wakeSessionStartTime
                if (timeSinceWake < 30 * 60 * 1000) { // 30 minutes
                    alarmPreferences.incrementStreak()
                    streakIncrementedThisSession = true
                }
            }
        }
    }
    
    fun cleanUpAndFinish() {
        viewModelScope.launch {
            alarmPreferences.clearAlarm()
            alarmPreferences.clearSleepStart()
        }
    }
    
    fun reportSnoozeOrDismissalWithoutTask() {
        viewModelScope.launch {
            if (!streakIncrementedThisSession) {
                alarmPreferences.resetStreak()
            }
        }
    }
}

class WakeViewModelFactory(
    private val taskRepository: TaskRepository,
    private val alarmPreferences: AlarmPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WakeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WakeViewModel(taskRepository, alarmPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
