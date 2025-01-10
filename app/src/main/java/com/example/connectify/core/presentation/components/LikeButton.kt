package com.example.connectify.core.presentation.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeMediumSmall
import kotlinx.coroutines.launch

@Composable
fun LikeButton(
    isLiked: Boolean = false,
    size: Dp = IconSizeMediumSmall,
    likedColor: Color = Color(0xFF08FF04),
    unLikedColor: Color = Color.White,
    onLikeClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val heartScale = remember { Animatable(1f) }
    val heartColor = remember { Animatable(if(isLiked) likedColor else unLikedColor) }

    // Handle color animation when isLiked changes
    LaunchedEffect(isLiked) {
        heartColor.animateTo(
            targetValue = if(isLiked) likedColor else unLikedColor,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f)
        )
    }

    fun animateHeart() {
        scope.launch {
            // Bounce animation
            heartScale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(
                    dampingRatio = 0.5f,  // More bouncy
                    stiffness = 1500f     // Faster animation
                )
            )
            heartScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.5f,
                    stiffness = 1500f
                )
            )
        }
    }

    Icon(
        painter = if(isLiked) {
            painterResource(id = R.drawable.ic_like)
        } else painterResource(id = R.drawable.ic_unlike),
        contentDescription = stringResource(
            id = if(isLiked) R.string.unlike else R.string.like
        ),
        tint = heartColor.value,
        modifier = Modifier
            .size(size)
            .scale(heartScale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onLikeClick()
                        animateHeart()
                    }
                )
            }
    )
}