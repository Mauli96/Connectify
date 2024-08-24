package com.example.connectify.feature_chat.presentation.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.core.presentation.ui.theme.DarkerGreen
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OwnMessage(
    message: String,
    formattedTime: String,
    color: Color = DarkerGreen,
    scope: CoroutineScope = rememberCoroutineScope(),
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    triangleWidth: Dp = 30.dp,
    triangleHeight: Dp = 30.dp,
    onLongPress: () -> Unit = {}
) {
    val cornerRadius = MaterialTheme.shapes.medium.bottomEnd
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.medium
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            scope.launch {
                                onLongPress()
                            }
                        }
                    )
                }
                .padding(SpaceMedium)
                .drawBehind {
                    val cornerRadiusPx = cornerRadius.toPx(
                        shapeSize = size,
                        density = Density(density)
                    )
                    val path = Path().apply {
                        moveTo(
                            size.width,
                            size.height - cornerRadiusPx
                        )
                        lineTo(size.width, size.height + triangleHeight.toPx())
                        lineTo(
                            size.width - triangleWidth.toPx(),
                            size.height - cornerRadiusPx
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        color = color
                    )
                }
        ) {
            Column {
                Text(
                    text = message,
                    color = textColor
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(
                        text = formattedTime,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}