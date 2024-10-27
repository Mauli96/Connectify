package com.example.connectify.core.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.connectify.core.presentation.ui.theme.DarkerGreen
import com.example.connectify.core.presentation.ui.theme.GreenAccent

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 35.dp,
    strokeWidth: Dp = 3.dp
) {
    val gradientBrush = Brush.sweepGradient(
        colors = listOf(
            DarkerGreen,
            GreenAccent
        )
    )
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = LinearEasing)
        ), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .rotate(rotation)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = 270f
            drawArc(
                brush = gradientBrush,
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}