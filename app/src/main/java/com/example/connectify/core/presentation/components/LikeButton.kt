package com.example.connectify.core.presentation.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeMediumSmall
import kotlinx.coroutines.launch


@Composable
fun LikeButton(
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val heartScale = remember {
        Animatable(1f)
    }
    val heartColor = remember {
        Animatable(Color(0xFF08FF04))
    }

    fun animateHeart() {
        scope.launch {
            if(isLiked) {
                heartScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 1f, stiffness = 3000f)
                )
                heartScale.animateTo(1f)
            } else {
                heartColor.animateTo(Color(0xFF08FF04))
                heartScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 1f, stiffness = 3000f)
                )
                heartScale.animateTo(1f)
            }
        }
    }

    Icon(
        painter = if(isLiked) {
            painterResource(id = R.drawable.ic_like)
        } else {
            painterResource(id = R.drawable.ic_unlike)
        },
        contentDescription = if(isLiked) {
            stringResource(id = R.string.unlike)
        } else {
            stringResource(id = R.string.like)
        },
        tint = if(isLiked) {
            heartColor.value
        } else {
            Color.White
        },
        modifier = Modifier
            .size(IconSizeMediumSmall)
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