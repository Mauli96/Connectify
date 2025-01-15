package com.example.connectify.feature_post.presentation.save_post

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.LottieIconSize
import com.example.connectify.core.presentation.ui.theme.SpaceLargeExtra
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Screen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SavedPostScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    viewModel: SavedPostViewModel = hiltViewModel()
) {
    val pagingPostState by viewModel.pagingPostState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_activity))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context)
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                title = {
                    Text(
                        text = stringResource(id = R.string.saved),
                        style = Typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                showBackArrow = true,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(pagingPostState.items.isEmpty() && !pagingPostState.isFirstLoading && !pagingPostState.isNextLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = SpaceLargeExtra),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimation(
                            modifier = Modifier.size(LottieIconSize),
                            composition = composition,
                            progress = { progress }
                        )
                        Text(
                            text = stringResource(R.string.no_saved_post),
                            style = Typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            count = pagingPostState.items.size,
                            key = { i ->
                                val post = pagingPostState.items[i]
                                post.id
                            }
                        ) { i ->
                            val post = pagingPostState.items[i]
                            if (i >= pagingPostState.items.size - 1 && pagingPostState.items.size >= Constants.DEFAULT_PAGE_SIZE
                                && !pagingPostState.endReached && !pagingPostState.isFirstLoading && !pagingPostState.isNextLoading) {
                                viewModel.loadNextPosts()
                            }
                            PostImageItem(
                                imageLoader = imageLoader,
                                post = post,
                                onPostImageClick = {
                                    onNavigate(Screen.PostDetailScreen.route + "/${post.id}")
                                }
                            )
                        }
                        if(pagingPostState.isNextLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = SpaceMedium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CustomCircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
                ConnectivityBanner(
                    networkState = networkState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }
        if(pagingPostState.isFirstLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}

@Composable
fun PostImageItem(
    imageLoader: ImageLoader,
    post: Post,
    onPostImageClick: () -> Unit = {}
) {
    AsyncImage(
        model = post.imageUrl,
        contentDescription = stringResource(R.string.post_image),
        imageLoader = imageLoader,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onPostImageClick()
                    }
                )
            },
        contentScale = ContentScale.Crop
    )
}