package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WakeSync", "Alarm Received! Action: ${intent.action}")
        
        // When alarm triggers, we want to launch the WakeActivity or MainActivity with a specific extra
        val wakeIntent = Intent(context, com.example.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_WAKE_UP", true)
        }
        
        context.startActivity(wakeIntent)
    }
}
