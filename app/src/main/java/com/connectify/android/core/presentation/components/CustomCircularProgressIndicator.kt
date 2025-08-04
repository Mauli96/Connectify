package com.connectify.android.core.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.connectify.android.core.presentation.ui.theme.DarkerGreen
import com.connectify.android.core.presentation.ui.theme.GreenAccent

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    outerCircleColor: Color = GreenAccent,
    innerCircleColor: Color = DarkerGreen,
    strokeWidth: Float = 7f,
    gap: Float = 5f,
    progressDuration: Int = 1500
) {
    val outerProgress = remember { Animatable(0f) }
    val innerProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        outerProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = progressDuration,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    LaunchedEffect(Unit) {
        innerProgress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = progressDuration,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(
        modifier = modifier.size(35.dp)
    ) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2

        drawArc(
            color = outerCircleColor,
            startAngle = -90f + (outerProgress.value * 360f),
            sweepAngle = outerProgress.value * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        val adjustedRadius = radius - strokeWidth - gap
        drawArc(
            color = innerCircleColor,
            startAngle = -90f - (innerProgress.value * 360f),
            sweepAngle = -(innerProgress.value * 360f),
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(
                x = strokeWidth + gap,
                y = strokeWidth + gap
            ),
            size = Size(
                width = adjustedRadius * 2,
                height = adjustedRadius * 2
            )
        )
    }
}