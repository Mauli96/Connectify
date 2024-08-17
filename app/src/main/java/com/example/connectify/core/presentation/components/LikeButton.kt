package com.example.connectify.core.presentation.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeLarge
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
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
            if(!isLiked) {
                heartScale.animateTo(
                    targetValue = 1.3f,
                    animationSpec = spring(dampingRatio = 1f, stiffness = 800f)
                )
                heartScale.animateTo(1f)
            } else {
                heartColor.animateTo(Color(0xFF08FF04))
            }
        }
    }

    IconButton(
        onClick = {
            onLikeClick()
            animateHeart()
        },
        modifier = Modifier
            .size(IconSizeLarge)
    ) {
        Icon(
            painter = if(isLiked) {
                painterResource(id = R.drawable.like_icon)
            } else {
                painterResource(id = R.drawable.unlike_icon)
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
                .size(IconSizeSmall)
                .scale(heartScale.value)
        )
    }
}