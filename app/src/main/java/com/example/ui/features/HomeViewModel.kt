package com.example.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AlarmPreferences
import com.example.data.FatigueEngine
import com.example.data.FatigueState
import com.example.data.Task
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val alarmTimeMillis: Long? = null,
    val isAlarmSet: Boolean = false,
    val currentStreak: Int = 0,
    val fatigueState: FatigueState = FatigueState(false, null)
)

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val alarmPreferences: AlarmPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        taskRepository.allTasks,
        alarmPreferences.alarmTime,
        alarmPreferences.isAlarmSet,
        alarmPreferences.currentStreak
    ) { tasks, time, isSet, streak ->
        val fatigue = FatigueEngine.evaluateCognitiveFatigue(tasks)
        HomeUiState(tasks, time, isSet, streak, fatigue)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun addTask(content: String) {
        if (content.isNotBlank()) {
            viewModelScope.launch {
                taskRepository.insert(Task(content = content.trim()))
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            taskRepository.deleteById(id)
        }
    }

    fun setAlarmTime(hourOf: Int, minuteOf: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOf)
            set(Calendar.MINUTE, minuteOf)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        viewModelScope.launch {
            alarmPreferences.setAlarm(calendar.timeInMillis)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val isNowCompleted = !task.isCompleted
            taskRepository.update(
                task.copy(
                    isCompleted = isNowCompleted,
                    completedAt = if (isNowCompleted) System.currentTimeMillis() else null
                )
            )
        }
    }

    fun cancelAlarm() {
        viewModelScope.launch {
            alarmPreferences.clearAlarm()
        }
    }
}

class HomeViewModelFactory(
    private val taskRepository: TaskRepository,
    private val alarmPreferences: AlarmPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(taskRepository, alarmPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
