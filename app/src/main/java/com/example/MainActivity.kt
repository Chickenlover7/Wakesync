package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.alarm.AlarmScheduler
import com.example.ui.features.HomeScreen
import com.example.ui.features.HomeViewModel
import com.example.ui.features.HomeViewModelFactory
import com.example.ui.features.SleepScreen
import com.example.ui.features.SleepViewModel
import com.example.ui.features.SleepViewModelFactory
import com.example.ui.features.WakeScreen
import com.example.ui.features.WakeViewModel
import com.example.ui.features.WakeViewModelFactory
import com.example.ui.features.FocusNapScreen
import com.example.ui.features.FocusNapViewModel
import com.example.ui.features.FocusNapViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request Notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val app = application as WakeSyncApplication
        val alarmScheduler = AlarmScheduler(this)

        val startDestination = if (intent?.getBooleanExtra("EXTRA_WAKE_UP", false) == true) {
            "wake"
        } else {
            "home"
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WakeSyncApp(
                        app = app,
                        alarmScheduler = alarmScheduler,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("EXTRA_WAKE_UP", false)) {
            val app = application as WakeSyncApplication
            val alarmScheduler = AlarmScheduler(this)
            setContent {
                MyApplicationTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        WakeSyncApp(
                            app = app,
                            alarmScheduler = alarmScheduler,
                            startDestination = "wake"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WakeSyncApp(
    app: WakeSyncApplication,
    alarmScheduler: AlarmScheduler,
    startDestination: String = "home"
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("home") {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(app.taskRepository, app.alarmPreferences)
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToSleep = {
                    navController.navigate("sleep") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToNap = {
                    navController.navigate("nap")
                },
                onScheduleAlarm = { timeInMillis ->
                    alarmScheduler.schedule(timeInMillis)
                },
                onCancelAlarm = {
                    alarmScheduler.cancel()
                }
            )
        }
        
        composable("nap") {
            val viewModel: FocusNapViewModel = viewModel(
                factory = FocusNapViewModelFactory(alarmScheduler)
            )
            FocusNapScreen(
                viewModel = viewModel,
                onCancel = {
                    navController.navigateUp()
                }
            )
        }
        
        composable("sleep") {
            val viewModel: SleepViewModel = viewModel(
                factory = SleepViewModelFactory(app.alarmPreferences)
            )
            SleepScreen(
                viewModel = viewModel,
                onCancelSleep = {
                    navController.navigate("home") {
                        popUpTo("sleep") { inclusive = true }
                    }
                }
            )
        }
        
        composable("wake") {
            val viewModel: WakeViewModel = viewModel(
                factory = WakeViewModelFactory(app.taskRepository, app.alarmPreferences)
            )
            WakeScreen(
                viewModel = viewModel,
                onFinishRoutine = {
                    navController.navigate("home") {
                        popUpTo("wake") { inclusive = true }
                    }
                }
            )
        }
    }
}
