package com.example.connectify.core.presentation.crop_image

import android.app.Activity
import android.net.Uri
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.connectify.R
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.crop_image.component.ItemIcon
import com.example.connectify.core.presentation.crop_image.cropview.CropType
import com.example.connectify.core.presentation.crop_image.cropview.EdgeType
import com.example.connectify.core.presentation.crop_image.cropview.ImageCrop
import com.example.connectify.core.presentation.ui.theme.DarkerGreen
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceMediumLarge
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import kotlinx.coroutines.launch

@Composable
fun CropScreen(
    onClose: () -> Unit = {},
    onNavigateUp: (String) -> Unit = {},
    viewModel: CropViewModel = hiltViewModel()
) {

    lateinit var imageCrop: ImageCrop
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    viewModel.getBitmapFromUrl(context)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onClose,
                showClose = true,
                title = {
                    Text(
                        text = stringResource(id = R.string.move_and_scale),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navActions = {
                    if(state.isSavingMediaToStore) {
                        CustomCircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = SpaceSmall)
                                .size(IconSizeSmall)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.apply),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .padding(end = SpaceSmall)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            viewModel.onEvent(CropEvents.CropImageBitmap(imageCrop.onCrop()))
                                            scope.launch {
                                                state.cropImageBitmap?.let { bm ->
                                                    viewModel.saveMediaToStorage(bm) { uri ->
                                                        onNavigateUp(Uri.encode(uri.toString()))
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                        )
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = SpaceMedium,
                    end = SpaceMedium
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemIcon(
                painterResource(id = R.drawable.ic_full_crop),
                description = stringResource(R.string.full_crop),
                cropType = CropType.FULL_PICTURE,
                currentCropType = state.cropType
            ) {
                viewModel.onEvent(CropEvents.ChangeCropType(CropType.FULL_PICTURE))
            }

            ItemIcon(
                painter = painterResource(id = R.drawable.ic_post_crop),
                description = stringResource(R.string.post_crop),
                cropType = CropType.POST_PICTURE,
                currentCropType = state.cropType
            ) {
                viewModel.onEvent(CropEvents.ChangeCropType(CropType.POST_PICTURE))
            }

            ItemIcon(
                painter = painterResource(id = R.drawable.ic_profile_crop),
                description = stringResource(R.string.profile_crop),
                cropType = CropType.PROFILE_PICTURE,
                currentCropType = state.cropType
            ) {
                viewModel.onEvent(CropEvents.ChangeCropType(CropType.PROFILE_PICTURE))
            }

            ItemIcon(
                painterResource(id = R.drawable.ic_reset),
                description = stringResource(R.string.reset_crop),
                cropType = CropType.RESET_PICTURE,
                currentCropType = state.cropType
            ) {
                viewModel.onEvent(CropEvents.ChangeCropType(CropType.RESET_PICTURE))
                imageCrop.resetView()
            }
        }
    }
}