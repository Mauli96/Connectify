package com.example.connectify.feature_post.presentation.create_post

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.components.StandardTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.GreenAccent
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.CropActivityResultContract
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.feature_post.presentation.util.PostConstants
import com.example.connectify.feature_post.presentation.util.PostDescriptionError
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    scaffoldState: ScaffoldState,
    viewModel: CreatePostViewModel = hiltViewModel()
) {

    val imageUri = viewModel.chosenImageUri.value

    val cropActivityLauncher = rememberLauncherForActivityResult(
        contract = CropActivityResultContract(16f, 9f)
    ) {
        viewModel.onEvent(CreatePostEvent.CropImage(it))
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if(it != null) {
            cropActivityLauncher.launch(it)
        }
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    GlobalScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = event.uiText.asString(context)
                        )
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                else -> {
                    null
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            onNavigateUp = onNavigateUp,
            showBackArrow = true,
            title = {
                Text(
                    text = stringResource(id = R.string.create_a_new_post),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceLarge)
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        galleryLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = stringResource(id = R.string.choose_image),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(IconSizeMedium)
                )
                imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = uri,
                            imageLoader = imageLoader
                        ),
                        contentDescription = stringResource(id = R.string.post_image),
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = viewModel.descriptionState.value.text,
                hint = stringResource(id = R.string.description),
                error = when(viewModel.descriptionState.value.error) {
                    is PostDescriptionError.FieldEmpty -> {
                        stringResource(id = R.string.this_field_cant_be_empty)
                    }
                    else -> ""
                },
                singleLine = false,
                maxLines = 5,
                minLines = 3,
                maxLength = PostConstants.MAX_POST_DESCRIPTION_LENGTH,
                onValueChange = {
                    viewModel.onEvent(CreatePostEvent.EnterDescription(it))
                }
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
            Button(
                onClick = {
                    viewModel.onEvent(CreatePostEvent.PostImage)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenAccent
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .height(40.dp)
                    .width(100.dp)
            ) {
                if(viewModel.isLoading.value) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(CenterVertically),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.post),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Icon(
                        painter = painterResource(id = R.drawable.post_icon),
                        contentDescription = null,
                        modifier = Modifier.size(IconSizeSmall)
                    )
                }
            }
        }
    }
}