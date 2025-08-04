package com.connectify.android.feature_post.presentation.main_feed

import android.content.Context
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.domain.models.Comment
import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.domain.states.PagingState
import com.connectify.android.core.domain.states.StandardTextFieldState
import com.connectify.android.core.presentation.components.Comment
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.PaginatedBottomSheet
import com.connectify.android.core.presentation.components.Post
import com.connectify.android.core.presentation.components.ShimmerListPostItem
import com.connectify.android.core.presentation.components.StandardBottomSheet
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.LottieIconSize
import com.connectify.android.core.presentation.ui.theme.SpaceLargeExtra
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.util.Constants
import com.connectify.android.core.util.Screen
import com.connectify.android.core.util.sendSharePostIntent
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    viewModel: MainFeedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagingPostState by viewModel.pagingPostState.collectAsStateWithLifecycle()
    val pagingCommentState by viewModel.pagingCommentState.collectAsStateWithLifecycle()
    val commentTextFieldState by  viewModel.commentTextFieldState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val focusRequester = remember { FocusRequester() }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_posts_found))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { viewModel.onRefreshPosts() }
    )

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when(event) {
            is UiEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.uiText.asString(context),
                    actionLabel = "VIEW"
                )
            }
            else -> {}
        }
    }

    LaunchedEffect(state.isNavigatedToPersonListScreen) {
        if(state.isNavigatedToPersonListScreen) {
            bottomSheetState.hide()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MainFeedToolbar(
            onNavigateUp = onNavigateUp,
            onSearchClick = { onNavigate(Screen.SearchScreen.route) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .swipeToSearch(
                    isNavigated = state.isNavigatedToSearchScreen,
                    onEvent = { viewModel.onEvent(MainFeedEvent.OnNavigatedToSearchScreen) },
                    onNavigate = { onNavigate(Screen.SearchScreen.route) }
                )
        ) {
            if(pagingPostState.items.isEmpty() && !pagingPostState.isFirstLoading && !pagingPostState.isNextLoading) {
                EmptyFeedContent(
                    composition = composition,
                    progress = progress
                )
            } else {
                PostsList(
                    pagingPostState = pagingPostState,
                    imageLoader = imageLoader,
                    onNavigate = onNavigate,
                    viewModel = viewModel,
                    state = state,
                    context = context
                )
            }
            CommentSheetContent(
                pagingCommentState = pagingCommentState,
                onNavigate = onNavigate,
                context = context,
                imageLoader = imageLoader,
                viewModel = viewModel,
                focusRequester = focusRequester,
                bottomSheetState = bottomSheetState,
                commentTextFieldState = commentTextFieldState,
                state = state
            )
            if(state.isBottomSheetVisible) {
                StandardBottomSheet(
                    title = state.selectedPostUsername.toString(),
                    showDownloadOption = true,
                    showDeleteOption = state.isOwnPost == true,
                    bottomSheetState = bottomSheetState,
                    onDismissRequest = {
                        viewModel.onEvent(MainFeedEvent.OnDismissBottomSheet)
                    },
                    onDownloadClick = {
                        viewModel.onEvent(MainFeedEvent.OnDownloadPost)
                        viewModel.onEvent(MainFeedEvent.OnDismissBottomSheet)
                    },
                    onCancelClick = {
                        viewModel.onEvent(MainFeedEvent.OnDismissBottomSheet)
                    }
                )
            }
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            ConnectivityBanner(
                networkState = networkState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun MainFeedToolbar(
    onNavigateUp: () -> Unit,
    onSearchClick: () -> Unit
) {
    StandardToolbar(
        onNavigateUp = onNavigateUp,
        title = {
            Text(
                text = stringResource(id = R.string.your_feed),
                style = Typography.titleLarge
            )
        },
        modifier = Modifier.fillMaxWidth(),
        navActions = {
            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(id = R.string.search_for_users),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(IconSizeSmall)
                )
            }
        }
    )
}

@Composable
private fun EmptyFeedContent(
    composition: LottieComposition?,
    progress: Float
) {
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
            text = stringResource(R.string.feed_empty),
            style = Typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PostsList(
    pagingPostState: PagingState<Post>,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit,
    viewModel: MainFeedViewModel,
    state: MainFeedState,
    context: Context
) {
    ShimmerListPostItem(
        isLoadingPost = pagingPostState.isFirstLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceMedium)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = pagingPostState.items.size,
                key = { i ->
                    pagingPostState.items[i].id
                }
            ) { i ->
                val post = pagingPostState.items[i]
                if(shouldLoadNextPage(i, pagingPostState)) {
                    viewModel.loadNextPosts()
                }
                PostItem(
                    post = post,
                    imageLoader = imageLoader,
                    onNavigate = onNavigate,
                    viewModel = viewModel,
                    context = context,
                    state = state
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            if(pagingPostState.isNextLoading) {
                item {
                    LoadingIndicator()
                }
            }
        }
    }
}

private fun shouldLoadNextPage(
    currentIndex: Int,
    pagingState: PagingState<Post>
): Boolean =
    currentIndex >= pagingState.items.size - 1 &&
            pagingState.items.size >= Constants.DEFAULT_PAGE_SIZE &&
            !pagingState.endReached &&
            !pagingState.isFirstLoading && !pagingState.isNextLoading

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SpaceMedium),
        contentAlignment = Alignment.Center
    ) {
        CustomCircularProgressIndicator()
    }
}

@Composable
private fun PostItem(
    post: Post,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit,
    viewModel: MainFeedViewModel,
    context: Context,
    state: MainFeedState
) {
    Post(
        post = post,
        imageLoader = imageLoader,
        onUsernameClick = {
            onNavigate(Screen.ProfileScreen.route + "?userId=${post.userId}")
        },
        onLikeClick = {
            viewModel.onEvent(MainFeedEvent.OnLikedPost(post.id))
        },
        onCommentClick = {
            viewModel.onEvent(MainFeedEvent.OnSelectPostId(post.id))
            viewModel.onEvent(MainFeedEvent.OnShowCommentBottomSheet)
            viewModel.onEvent(MainFeedEvent.OnLoadComments)
        },
        onShareClick = {
            context.sendSharePostIntent(post.id)
        },
        onSaveClick = {
            viewModel.onEvent(MainFeedEvent.OnSavePost(post.id))
        },
        onLikedByClick = {
            onNavigate(Screen.PersonListScreen.route + "/${post.id}")
        },
        onMoreItemClick = {
            viewModel.onEvent(MainFeedEvent.OnSelectPostId(post.id))
            viewModel.onEvent(MainFeedEvent.OnSelectPostUsername(post.username, post.isOwnPost))
            viewModel.onEvent(MainFeedEvent.OnShowBottomSheet)
        },
        isDescriptionVisible = state.isDescriptionVisible[post.id] ?: false,
        onDescriptionToggle = {
            viewModel.onEvent(MainFeedEvent.OnDescriptionToggle(post.id))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheetContent(
    pagingCommentState: PagingState<Comment>,
    onNavigate: (String) -> Unit,
    context: Context,
    imageLoader: ImageLoader,
    viewModel: MainFeedViewModel,
    focusRequester: FocusRequester,
    bottomSheetState: SheetState,
    commentTextFieldState: StandardTextFieldState,
    state: MainFeedState
) {
    if(state.isCommentBottomSheetVisible) {
        PaginatedBottomSheet(
            title = stringResource(R.string.comments),
            bottomSheetState = bottomSheetState,
            onDismissBottomSheet = {
                viewModel.onEvent(MainFeedEvent.OnDismissCommentBottomSheet)
            },
            imageLoader = imageLoader,
            items = pagingCommentState.items,
            isFirstLoading = pagingCommentState.isFirstLoading,
            isNextLoading = pagingCommentState.isNextLoading,
            endReached = pagingCommentState.endReached,
            loadNextPage = {
                viewModel.loadNextComments()
            },
            selectedFilter = state.commentFilter,
            onFilterSelected = { filterType ->
                viewModel.onEvent(MainFeedEvent.OnChangeCommentFilter(filterType))
            },
            isDropdownMenuExpanded = state.isDropdownMenuVisible,
            onShowDropDownMenu = {
                viewModel.onEvent(MainFeedEvent.OnShowDropDownMenu)
            },
            onDismissDropdownMenu = {
                viewModel.onEvent(MainFeedEvent.OnDismissDropDownMenu)
            },
            keyExtractor = { comment ->
                comment.id
            },
            textFieldState = commentTextFieldState,
            onValueChange = {
                viewModel.onEvent(MainFeedEvent.OnEnteredComment(it))
            },
            onSend = {
                viewModel.onEvent(MainFeedEvent.OnComment)
            },
            ownProfilePicture = state.profilePicture ?: "",
            hint = stringResource(R.string.enter_a_comment),
            isUploading = state.isUploading,
            focusRequester = focusRequester
        ) { _, comment ->
            Comment(
                modifier = Modifier.fillMaxWidth(),
                comment = comment,
                context = context,
                imageLoader = imageLoader,
                onLikeClick = {
                    viewModel.onEvent(MainFeedEvent.OnLikedComment(comment.id))
                },
                onLikedByClick = {
                    viewModel.onEvent(MainFeedEvent.OnNavigatedToPersonListScreen)
                    onNavigate(Screen.PersonListScreen.route + "/${comment.id}")
                },
                onLongPress = {
                    viewModel.onEvent(MainFeedEvent.OnSelectComment(comment.id))
                },
                onDeleteClick = {
                    viewModel.onEvent(MainFeedEvent.OnDeleteComment)
                }
            )
        }
    }
}

fun Modifier.swipeToSearch(
    isNavigated: Boolean,
    onEvent: () -> Unit,
    onNavigate: () -> Unit
) = pointerInput(Unit) {
    var totalDragAmount = 0f
    val minSwipeDistance = 50.dp.toPx()
    val velocityThreshold = 800f

    detectHorizontalDragGestures(
        onDragStart = {
            totalDragAmount = 0f
        },
        onDragEnd = {
            if(abs(totalDragAmount) >= minSwipeDistance && !isNavigated) {
                onEvent()
                onNavigate()
            }
            totalDragAmount = 0f
        },
        onHorizontalDrag = { change, dragAmount ->
            change.consume()
            totalDragAmount += dragAmount

            if(abs(dragAmount) > velocityThreshold && !isNavigated) {
                onEvent()
                onNavigate()
            }
        }
    )
}

