package com.example.connectify.core.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    var oldCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()
        for(i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if(oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if(oldCount < count) {
                        slideInVertically { it } togetherWith slideOutVertically { -it }
                    } else {
                        slideInVertically { -it } togetherWith slideOutVertically { it }
                    }
                }, label = "counterAnimation"
            ) { char ->
                Text(
                    text = char.toString(),
                    style = style.copy(
                        fontSize = 17.sp
                    ),
                    softWrap = false,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onClick()
                                }
                            )
                        }
                )
            }
        }
    }
}