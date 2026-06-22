package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.neoBrutalism
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SleepScreen(
    viewModel: SleepViewModel,
    onCancelSleep: () -> Unit
) {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        viewModel.startSleepSession()
        while (true) {
            delay(1000)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Extremely dark for sleep mode
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bedtime,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = timeFormatter.format(Date(currentTimeMillis)),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = Color.DarkGray // Dim text
            )
            
            Text(
                text = "Sleep mode active. Alarm is set.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onCancelSleep,
                modifier = Modifier.neoBrutalism(cornerRadius = 100.dp, borderWidth = 2.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground)
            ) {
                Text(
                    text = "Cancel Sleep Mode",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
