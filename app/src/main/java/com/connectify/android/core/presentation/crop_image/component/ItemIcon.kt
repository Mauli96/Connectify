package com.connectify.android.core.presentation.crop_image.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import com.connectify.android.core.presentation.crop_image.cropview.CropType
import com.connectify.android.core.presentation.ui.theme.DarkerGreen
import com.connectify.android.core.presentation.ui.theme.GreenAccent
import com.connectify.android.core.presentation.ui.theme.IconSizeMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall

@Composable
fun ItemIcon(
    painter: Painter,
    description: String?,
    cropType: CropType,
    currentCropType: CropType,
    onClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painter,
            contentDescription = description,
            modifier = Modifier
                .size(IconSizeMedium)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onClick()
                        }
                    )
                }
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        if(currentCropType == cropType) {
            Box(
                modifier = Modifier
                    .size(SpaceSmall)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(
                                DarkerGreen,
                                GreenAccent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        } else {
            Box(modifier = Modifier.size(SpaceSmall))
        }
    }
}