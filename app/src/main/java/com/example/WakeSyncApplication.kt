package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.TaskRepository
import com.example.data.AlarmPreferences

class WakeSyncApplication : Application() {
    
    lateinit var database: AppDatabase
        private set
        
    lateinit var taskRepository: TaskRepository
        private set
        
    lateinit var alarmPreferences: AlarmPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "wakesync_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        
        taskRepository = TaskRepository(database.taskDao())
        alarmPreferences = AlarmPreferences(this)
    }
}
