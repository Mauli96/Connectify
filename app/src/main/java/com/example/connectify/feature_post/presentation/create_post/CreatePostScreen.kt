package com.example.connectify.feature_post.presentation.create_post

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.connectify.R
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.PulsatingLoadingText
import com.example.connectify.core.presentation.components.StandardOutlinedTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.crop_image.cropview.CropType
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.GreenAccent
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.ui.theme.withSize
import com.example.connectify.core.presentation.util.ObserveAsEvents
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_post.presentation.util.PostDescriptionError

@Composable
fun CreatePostScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    viewModel: CreatePostViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val descriptionState by viewModel.descriptionState.collectAsStateWithLifecycle()
    val navigatorState by viewModel.navigationState.collectAsState()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val galleryLauncher = rememberImagePicker(
        onImageSelected = { uri ->
            viewModel.onEvent(CreatePostEvent.OnNavigatingToCrop)
            navigateToCropScreen(navController, uri)
        }
    )

    HandleNavigationResults(
        navController = navController,
        lifecycleOwner = lifecycleOwner,
        viewModel = viewModel,
        onImageCropped = { uri ->
            viewModel.onEvent(CreatePostEvent.OnCropImage(uri))
        }
    )

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when(event) {
            is UiEvent.ShowSnackbar -> {
                keyboardController?.hide()
                snackbarHostState.showSnackbar(
                    message = event.uiText.asString(context)
                )
            }
            is UiEvent.Navigate -> {
                onNavigate(event.route)
            }
            else -> {}
        }
    }

    if(!navigatorState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CreatePostToolbar(
                    onNavigateUp = onNavigateUp,
                    isPosting = state.isPosting,
                    onPost = { viewModel.onEvent(CreatePostEvent.OnPostImage) }
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(SpaceLarge)
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(4f / 5f)
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = stringResource(R.string.create_post),
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            state.imageUri?.let { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = stringResource(id = R.string.post_image),
                                    imageLoader = imageLoader,
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(SpaceLarge))
                        Text(
                            text = stringResource(id = R.string.description),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            text = descriptionState.text,
                            onValueChange = {
                                viewModel.onEvent(CreatePostEvent.OnEnterDescription(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 5,
                            minLines = 3,
                            error = when(descriptionState.error) {
                                is PostDescriptionError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            }
                        )
                    }
                    ConnectivityBanner(
                        networkState = networkState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberImagePicker(
    onImageSelected: (Uri) -> Unit
): ActivityResultLauncher<PickVisualMediaRequest> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onImageSelected)
    }
}

@Composable
private fun HandleNavigationResults(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner,
    viewModel: CreatePostViewModel,
    onImageCropped: (Uri) -> Unit
) {
    DisposableEffect(key1 = navController) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(CreatePostEvent.OnNavigatingToBackFromCrop)
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("imageUri")?.let { encodedUri ->
                    try {
                        val decodedUri = Uri.parse(Uri.decode(encodedUri))
                        onImageCropped(decodedUri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("imageUri")
        }
    }
}

@Composable
private fun CreatePostToolbar(
    onNavigateUp: () -> Unit,
    isPosting: Boolean,
    onPost: () -> Unit
) {
    StandardToolbar(
        onNavigateUp = onNavigateUp,
        showClose = true,
        title = {
            Text(
                text = stringResource(id = R.string.create_a_new_post),
                style = Typography.titleLarge
            )
        },
        navActions = {
            if(isPosting) {
                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = SpaceMedium)
                        .size(IconSizeSmall)
                )
            } else {
                Text(
                    text = stringResource(R.string.post),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = GreenAccent
                    ),
                    modifier = Modifier
                        .padding(end = SpaceSmall)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onPost()
                                }
                            )
                        }
                )
            }
        }
    )
}

private fun navigateToCropScreen(
    navController: NavHostController,
    uri: Uri
) {
    val encodedUri = Uri.encode(uri.toString())
    navController.navigate("${Screen.CropScreen.route}/$encodedUri?cropType=${CropType.POST_PICTURE.name}") {
        launchSingleTop = true
        popUpTo(Screen.CreatePostScreen.route) {
            saveState = true
        }
    }
}