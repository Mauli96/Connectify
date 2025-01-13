package com.example.connectify.core.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeMediumSmall
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    size: Dp = IconSizeMediumSmall,
    likedColor: Color = Color(0xFF08FF04),
    unLikedColor: Color = Color.White
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val angle = remember { Animatable(0f) }

    val color by animateColorAsState(
        targetValue = if(isLiked) likedColor else unLikedColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "color"
    )

    // Optional explosion effect particles
    var showParticles by remember { mutableStateOf(false) }

    fun animateHeart() {
        scope.launch {
            // Scale up quickly
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )

            // Bounce back with rotation
            launch {
                angle.animateTo(
                    targetValue = 20f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                angle.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }

            // Scale back with bounce
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )

            if(isLiked) {
                showParticles = true
                launch {
                    delay(300)
                    showParticles = false
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Optional particle effect when liked
        if (showParticles && isLiked) {
            ParticleEffect(
                modifier = Modifier.matchParentSize()
            )
        }

        Icon(
            painter = painterResource(
                id = if(isLiked) R.drawable.ic_like else R.drawable.ic_unlike
            ),
            contentDescription = stringResource(
                id = if(isLiked) R.string.unlike else R.string.like
            ),
            tint = color,
            modifier = Modifier
                .size(size)
                .scale(scale.value)
                .graphicsLayer {
                    rotationZ = angle.value
                }
                .pointerInput(isLiked) {
                    detectTapGestures(
                        onTap = {
                            onLikeClick()
                            animateHeart()
                        }
                    )
                }
        )
    }
}

@Composable
private fun ParticleEffect(
    modifier: Modifier = Modifier
) {
    val particles = remember { List(6) { random -> ParticleState(random) } }

    Box(modifier = modifier) {
        particles.forEach { particle ->
            Particle(particle)
        }
    }
}

@Composable
private fun Particle(state: ParticleState) {
    val animatedScale = remember { Animatable(0f) }
    val animatedAlpha = remember { Animatable(1f) }
    val animatedOffset = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            animatedScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(500)
            )
        }
        launch {
            animatedAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(500)
            )
        }
        launch {
            animatedOffset.animateTo(
                targetValue = state.distance,
                animationSpec = tween(500)
            )
        }
    }

    Box(
        modifier = Modifier
            .size(4.dp)
            .graphicsLayer {
                scaleX = animatedScale.value
                scaleY = animatedScale.value
                alpha = animatedAlpha.value
                translationX = animatedOffset.value * cos(state.angle)
                translationY = animatedOffset.value * sin(state.angle)
            }
    )
}

private class ParticleState(random: Int) {
    val angle: Float = random * (2f * Math.PI / 6f).toFloat()
    val distance: Float = (20..40).random().toFloat()
}