package com.example.ui.features

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.ui.theme.neoBrutalism

@Composable
fun FocusNapScreen(
    viewModel: FocusNapViewModel,
    onCancel: () -> Unit
) {
    val timeLeft by viewModel.timeLeftMillis.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.startNapSession()
    }
    
    // Breathing animation
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Breathing Circle
                val circleColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                val strokeColor = MaterialTheme.colorScheme.secondary
                Canvas(modifier = Modifier.size(240.dp)) {
                    drawCircle(
                        color = circleColor,
                        radius = size.minDimension / 2 * scale
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = size.minDimension / 2 * scale,
                        style = Stroke(width = 4f)
                    )
                }
                
                // Timer Text
                val minutes = timeLeft / (1000 * 60)
                val seconds = (timeLeft / 1000) % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = "Power Nap Mode Active",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = {
                    viewModel.cancelNapSession()
                    onCancel()
                },
                modifier = Modifier.padding(top = 32.dp).neoBrutalism(cornerRadius = 100.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(
                    text = "End Nap",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
