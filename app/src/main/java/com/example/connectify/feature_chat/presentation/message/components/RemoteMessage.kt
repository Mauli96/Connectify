package com.example.connectify.feature_chat.presentation.message.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.feature_chat.domain.model.Message

@Composable
fun RemoteMessage(
    message: Message,
    formattedTime: String,
    color: Color = HintGray,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    triangleWidth: Dp = 30.dp,
    triangleHeight: Dp = 30.dp,
    tailOffset: Dp = 8.dp,
) {

    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.large
                )
                .drawBehind {
                    val triangleHeightPx = triangleHeight.toPx()
                    val triangleWidthPx = triangleWidth.toPx()
                    val tailOffsetPx = tailOffset.toPx()

                    val path = Path().apply {
                        moveTo(
                            0f + triangleWidthPx - tailOffsetPx,
                            0f
                        )
                        lineTo(
                            0f - tailOffsetPx,
                            0f
                        )
                        lineTo(
                            0f + triangleWidthPx - tailOffsetPx,
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
                    color = textColor
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(
                        text = formattedTime,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}