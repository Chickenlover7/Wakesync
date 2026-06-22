package com.example.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neoBrutalism(
    cornerRadius: Dp = 100.dp, // Pill shape default
    offsetSize: Dp = 6.dp,
    borderWidth: Dp = 4.dp,
    borderColor: Color = NeoBorder
): Modifier = this
    .drawBehind {
        drawRoundRect(
            color = borderColor,
            topLeft = Offset(offsetSize.toPx(), offsetSize.toPx()),
            size = size,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
    .border(
        width = borderWidth,
        color = borderColor,
        shape = RoundedCornerShape(cornerRadius)
    )
