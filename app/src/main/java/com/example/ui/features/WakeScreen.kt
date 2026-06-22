package com.example.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Task
import com.example.ui.theme.neoBrutalism
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun WakeScreen(
    viewModel: WakeViewModel,
    onFinishRoutine: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Good Morning.",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Here is your focus for today:",
                style = MaterialTheme.typography.titleLarge
            )

            if (uiState.tasks.isEmpty()) {
                Text(
                    text = "You don't have any tasks scheduled for this morning.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        WakeTaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) }
                        )
                    }
                }
            }

            // Sleep stats
            if (uiState.sleepDurationMillis > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth().neoBrutalism(cornerRadius = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Sleep Summary",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        val totalMinutes = uiState.sleepDurationMillis / (1000 * 60)
                        val hours = totalMinutes / 60
                        val minutes = totalMinutes % 60
                        Text(
                            text = "You slept for ${hours}h ${minutes}m",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.cleanUpAndFinish()
                    onFinishRoutine()
                },
                modifier = Modifier.fillMaxWidth().neoBrutalism(cornerRadius = 100.dp),
                shape = RoundedCornerShape(100.dp),
                enabled = uiState.allTasksCompleted || uiState.tasks.isEmpty()
            ) {
                if (uiState.allTasksCompleted || uiState.tasks.isEmpty()) {
                    Text("Start My Day", style = MaterialTheme.typography.labelLarge)
                } else {
                    Text("Complete all tasks to proceed", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun WakeTaskItem(
    task: Task,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().neoBrutalism(cornerRadius = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                modifier = Modifier.scale(1.2f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = task.content,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            if (task.isCompleted) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

