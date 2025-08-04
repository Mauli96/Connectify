package com.connectify.android.feature_chat.presentation.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.connectify.android.core.presentation.ui.theme.DarkerGreen
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withSize
import com.connectify.android.feature_chat.domain.model.Message
import kotlinx.coroutines.launch

@Composable
fun OwnMessage(
    message: Message,
    formattedTime: String,
    color: Color = DarkerGreen,
    modifier: Modifier = Modifier,
    triangleWidth: Dp = 30.dp,
    triangleHeight: Dp = 30.dp,
    tailOffset: Dp = 8.dp,
    onLongPress: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.large
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
                .drawBehind {
                    val path = Path().apply {
                        val triangleHeightPx = triangleHeight.toPx()
                        val triangleWidthPx = triangleWidth.toPx()
                        val tailOffsetPx = tailOffset.toPx()

                        moveTo(
                            size.width - triangleWidthPx + tailOffsetPx,
                            0f
                        )
                        lineTo(
                            size.width + tailOffsetPx,
                            0f
                        )
                        lineTo(
                            size.width - triangleWidthPx + tailOffsetPx,
                            triangleHeightPx
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        color = color
                    )
                }
                .padding(SpaceSmall)
        ) {
            Column {
                Text(
                    text = message.text,
                    style = Typography.labelMedium.withSize(14.sp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(
                        text = formattedTime,
                        style = Typography.labelSmall.withSize(10.sp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}