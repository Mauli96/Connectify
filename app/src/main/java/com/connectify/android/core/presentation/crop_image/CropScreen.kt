package com.connectify.android.core.presentation.crop_image

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.connectify.android.R
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.crop_image.component.ItemIcon
import com.connectify.android.core.presentation.crop_image.cropview.CropType
import com.connectify.android.core.presentation.crop_image.cropview.EdgeType
import com.connectify.android.core.presentation.crop_image.cropview.ImageCrop
import com.connectify.android.core.presentation.ui.theme.DarkerGreen
import com.connectify.android.core.presentation.ui.theme.GreenAccent
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceMediumLarge
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography

@Composable
fun CropScreen(
    onClose: () -> Unit = {},
    onNavigateUp: (String) -> Unit = {},
    viewModel: CropViewModel = hiltViewModel()
) {

    lateinit var imageCrop: ImageCrop
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CropToolbar(
                isSaving = state.isSavingMedia,
                onClose = onClose,
                onApply = {
                    viewModel.onEvent(CropEvents.OnCropImageBitmap(imageCrop.onCrop()))
                    viewModel.saveImage { uri ->
                        onNavigateUp(Uri.encode(uri.toString()))
                    }
                }
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SpaceMediumLarge,
                        end = SpaceMediumLarge,
                        top = SpaceSmall,
                        bottom = SpaceMediumLarge
                    ),
                contentAlignment = Alignment.Center
            ) {
                val maxWidth = maxWidth
                val maxHeight = maxHeight

                state.imageBitmap?.let { bm ->
                    val aspectRatio = bm.width.toFloat() / bm.height.toFloat()
                    val boxHeight = maxWidth / aspectRatio

                    Box(
                        modifier = Modifier
                            .width(maxWidth)
                            .height(boxHeight.coerceAtMost(maxHeight))
                            .align(Alignment.Center)
                    ) {
                        imageCrop = ImageCrop(bitmapImage = bm)
                        imageCrop.ImageCropView(
                            modifier = Modifier.fillMaxSize(),
                            guideLineColor = DarkerGreen,
                            guideLineWidth = 1.5.dp,
                            edgeCircleSize = 3.dp,
                            showGuideLines = state.cropType != CropType.PROFILE_PICTURE,
                            cropType = state.cropType,
                            edgeType = EdgeType.SQUARE
                        )
                    }
                }
            }
        }

        CropControls(
            currentCropType = state.cropType,
            onCropTypeChanged = { newType ->
                viewModel.onEvent(CropEvents.OnChangeCropType(newType))
                if(newType == CropType.RESET_PICTURE) {
                    imageCrop.resetView()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = SpaceMedium,
                    end = SpaceMedium
                )
        )
    }
}

@Composable
private fun CropToolbar(
    isSaving: Boolean,
    onClose: () -> Unit,
    onApply: () -> Unit
) {
    StandardToolbar(
        onNavigateUp = onClose,
        showClose = true,
        title = {
            Text(
                text = stringResource(id = R.string.move_and_scale),
                style = Typography.titleLarge
            )
        },
        navActions = {
            if(isSaving) {
                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = SpaceMedium)
                        .size(IconSizeSmall)
                )
            } else {
                Text(
                    text = stringResource(R.string.apply),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GreenAccent
                    ),
                    modifier = Modifier
                        .padding(end = SpaceSmall)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onApply()
                                }
                            )
                        }
                )
            }
        }
    )
}

@Composable
private fun CropControls(
    modifier: Modifier,
    currentCropType: CropType,
    onCropTypeChanged: (CropType) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CropControlItem(
            iconRes = R.drawable.ic_full_crop,
            description = R.string.full_crop,
            cropType = CropType.FULL_PICTURE,
            currentCropType = currentCropType,
            onCropTypeChanged = onCropTypeChanged
        )

        CropControlItem(
            iconRes = R.drawable.ic_post_crop,
            description = R.string.post_crop,
            cropType = CropType.POST_PICTURE,
            currentCropType = currentCropType,
            onCropTypeChanged = onCropTypeChanged
        )

        CropControlItem(
            iconRes = R.drawable.ic_profile_crop,
            description = R.string.profile_crop,
            cropType = CropType.PROFILE_PICTURE,
            currentCropType = currentCropType,
            onCropTypeChanged = onCropTypeChanged
        )

        CropControlItem(
            iconRes = R.drawable.ic_reset,
            description = R.string.reset_crop,
            cropType = CropType.RESET_PICTURE,
            currentCropType = currentCropType,
            onCropTypeChanged = onCropTypeChanged
        )
    }
}

@Composable
private fun CropControlItem(
    @DrawableRes iconRes: Int,
    @StringRes description: Int,
    cropType: CropType,
    currentCropType: CropType,
    onCropTypeChanged: (CropType) -> Unit
) {
    ItemIcon(
        painter = painterResource(id = iconRes),
        description = stringResource(description),
        cropType = cropType,
        currentCropType = currentCropType,
        onClick = { onCropTypeChanged(cropType) }
    )
}