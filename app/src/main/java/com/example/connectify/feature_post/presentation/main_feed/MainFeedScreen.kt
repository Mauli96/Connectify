package com.example.connectify.feature_post.presentation.main_feed

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
import androidx.compose.ui.Alignment.Companion.Center
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.components.Comment
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.PaginatedBottomSheet
import com.example.connectify.core.presentation.components.Post
import com.example.connectify.core.presentation.components.ShimmerListPostItem
import com.example.connectify.core.presentation.components.StandardBottomSheet
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.LottieIconSize
import com.example.connectify.core.presentation.ui.theme.SpaceLargeExtra
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.sendSharePostIntent
import kotlinx.coroutines.flow.collectLatest

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

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val focusRequester = remember {
        FocusRequester()
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_posts_found))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = {
            viewModel.onRefreshPosts()
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context),
                        actionLabel = "VIEW"
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

    LaunchedEffect(state.isNavigatedToPersonListScreen) {
        if(state.isNavigatedToPersonListScreen) {
            bottomSheetState.hide()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        StandardToolbar(
            onNavigateUp = onNavigateUp,
            title = {
                Text(
                    text = stringResource(id = R.string.your_feed),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            modifier = Modifier.fillMaxWidth(),
            navActions = {
                IconButton(
                    onClick = {
                        onNavigate(Screen.SearchScreen.route)
                    }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if(dragAmount < 1.dp.toPx() && !state.isNavigatedToSearchScreen) {
                            viewModel.onEvent(MainFeedEvent.NavigatedToSearchScreen)
                            onNavigate(Screen.SearchScreen.route)
                        }
                    }
                }
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
                        progress = {
                            progress
                        },
                    )
                    Text(
                        text = stringResource(R.string.feed_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }else {
                ShimmerListPostItem(
                    isLoadingPost = pagingPostState.isFirstLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = SpaceSmall,
                            start = SpaceSmall,
                            end = SpaceSmall
                        )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(
                            count = pagingPostState.items.size,
                            key = { i ->
                                val post = pagingPostState.items[i]
                                post.id
                            }
                        ) { i ->
                            val post = pagingPostState.items[i]
                            if(i >= pagingPostState.items.size - 1 && pagingPostState.items.size >= Constants.DEFAULT_PAGE_SIZE
                                && !pagingPostState.endReached && !pagingPostState.isFirstLoading && !pagingPostState.isNextLoading) {
                                viewModel.loadNextPosts()
                            }
                            Post(
                                post = post,
                                imageLoader = imageLoader,
                                onUsernameClick = {
                                    onNavigate(Screen.ProfileScreen.route + "?userId=${post.userId}")
                                },
                                onLikeClick = {
                                    viewModel.onEvent(MainFeedEvent.LikedPost(post.id))
                                },
                                onCommentClick = {
                                    viewModel.onEvent(MainFeedEvent.SelectPostId(post.id))
                                    viewModel.onEvent(MainFeedEvent.ShowCommentBottomSheet)
                                    viewModel.onEvent(MainFeedEvent.LoadComments)
                                },
                                onShareClick = {
                                    context.sendSharePostIntent(post.id)
                                },
                                onSaveClick = {
                                    viewModel.onEvent(MainFeedEvent.SavePost(post.id))
                                },
                                onLikedByClick = {
                                    onNavigate(Screen.PersonListScreen.route + "/${post.id}")
                                },
                                onMoreItemClick = {
                                    viewModel.onEvent(MainFeedEvent.SelectPostId(post.id))
                                    viewModel.onEvent(MainFeedEvent.SelectPostUsername(post.username, post.isOwnPost))
                                    viewModel.onEvent(MainFeedEvent.ShowBottomSheet)
                                },
                                isDescriptionVisible = state.isDescriptionVisible[post.id] ?: false,
                                onDescriptionToggle = {
                                    viewModel.onEvent(MainFeedEvent.OnDescriptionToggle(post.id))
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
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
            }
            CommentSheetContent(
                pagingCommentState = pagingCommentState,
                imageLoader = imageLoader,
                onNavigate = onNavigate,
                context = context,
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
                        viewModel.onEvent(MainFeedEvent.DismissBottomSheet)
                    },
                    onDownloadClick = {
                        viewModel.onEvent(MainFeedEvent.DownloadPost)
                        viewModel.onEvent(MainFeedEvent.DismissBottomSheet)
                    },
                    onCancelClick = {
                        viewModel.onEvent(MainFeedEvent.DismissBottomSheet)
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
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSheetContent(
    pagingCommentState: PagingState<Comment>,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit,
    context: Context,
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
                viewModel.onEvent(MainFeedEvent.DismissCommentBottomSheet)
            },
            items = pagingCommentState.items,
            isFirstLoading = pagingCommentState.isFirstLoading,
            isNextLoading = pagingCommentState.isNextLoading,
            endReached = pagingCommentState.endReached,
            loadNextPage = {
                viewModel.loadNextComments()
            },
            selectedFilter = state.commentFilter,
            onFilterSelected = { filterType ->
                viewModel.onEvent(MainFeedEvent.ChangeCommentFilter(filterType))
            },
            isDropdownMenuExpanded = state.isDropdownMenuVisible,
            onShowDropDownMenu = {
                viewModel.onEvent(MainFeedEvent.ShowDropDownMenu)
            },
            onDismissDropdownMenu = {
                viewModel.onEvent(MainFeedEvent.DismissDropDownMenu)
            },
            keyExtractor = { comment ->
                comment.id
            },
            textFieldState = commentTextFieldState,
            onValueChange = {
                viewModel.onEvent(MainFeedEvent.EnteredComment(it))
            },
            onSend = {
                viewModel.onEvent(MainFeedEvent.Comment)
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
                    viewModel.onEvent(MainFeedEvent.LikedComment(comment.id))
                },
                onLikedByClick = {
                    viewModel.onEvent(MainFeedEvent.NavigatedToPersonListScreen)
                    onNavigate(Screen.PersonListScreen.route + "/${comment.id}")
                },
                onLongPress = {
                    viewModel.onEvent(MainFeedEvent.SelectComment(comment.id))
                },
                onDeleteClick = {
                    viewModel.onEvent(MainFeedEvent.DeleteComment)
                }
            )
        }
    }
}