package com.example.connectify.feature_profile.presentation.profile

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.User
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.components.Comment
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.PaginatedBottomSheet
import com.example.connectify.core.presentation.components.Post
import com.example.connectify.core.presentation.components.StandardBottomSheet
import com.example.connectify.core.presentation.ui.theme.LottieIconSize
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeLarge
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.openUrlInBrowser
import com.example.connectify.core.util.sendSharePostIntent
import com.example.connectify.core.util.toPx
import com.example.connectify.feature_profile.presentation.profile.components.BannerSection
import com.example.connectify.feature_profile.presentation.profile.components.ProfileHeaderSection
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onLogout: () -> Unit = {},
    profilePictureSize: Dp = ProfilePictureSizeLarge,
    viewModel: ProfileViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val toolbarState by viewModel.toolbarState.collectAsStateWithLifecycle()
    val pagingPostState by viewModel.pagingPostState.collectAsStateWithLifecycle()
    val pagingCommentState by viewModel.pagingCommentState.collectAsStateWithLifecycle()
    val commentTextFieldState by  viewModel.commentTextFieldState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val focusRequester = remember {
        FocusRequester()
    }

    val iconHorizontalCenterLength =
        (LocalConfiguration.current.screenWidthDp.dp.toPx() / 4f -
                (profilePictureSize / 4f).toPx() -
                SpaceSmall.toPx()) / 2f
    val iconSizeExpanded = 35.dp
    val toolbarHeightCollapsed = 75.dp
    val imageCollapsedOffsetY = remember {
        (toolbarHeightCollapsed - profilePictureSize / 2f) / 2f
    }
    val iconCollapsedOffsetY = remember {
        (toolbarHeightCollapsed - iconSizeExpanded) / 2f
    }
    val bannerHeight = (LocalConfiguration.current.screenWidthDp / 2.5f).dp
    val toolbarHeightExpanded = remember {
        bannerHeight + profilePictureSize
    }
    val maxOffset = remember {
        toolbarHeightExpanded - toolbarHeightCollapsed
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                val shouldNotScroll = delta > 0f && lazyListState.firstVisibleItemIndex != 0 ||
                        viewModel.pagingPostState.value.items.isEmpty()
                if(shouldNotScroll) {
                    return Offset.Zero
                }
                val newOffset = viewModel.toolbarState.value.toolbarOffsetY + delta
                viewModel.setToolbarOffsetY(
                    newOffset.coerceIn(
                        minimumValue = -maxOffset.toPx(),
                        maximumValue = 0f
                    )
                )
                viewModel.setExpandedRatio((viewModel.toolbarState.value.toolbarOffsetY + maxOffset.toPx()) / maxOffset.toPx())
                return Offset.Zero
            }
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_posts_for_profile))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyListState
        ) {
            item {
                Spacer(
                    modifier = Modifier.height(
                        toolbarHeightExpanded - profilePictureSize / 2f
                    )
                )
            }
            item {
                state.profile?.let { profile ->
                    ProfileHeaderSection(
                        user = User(
                            userId = profile.userId,
                            profilePictureUrl = profile.profilePictureUrl,
                            username = profile.username,
                            description = profile.bio,
                            followerCount = profile.followerCount,
                            followingCount = profile.followingCount,
                            postCount = profile.postCount
                        ),
                        isFollowing = profile.isFollowing,
                        isOwnProfile = profile.isOwnProfile,
                        onFollowingClick = {
                            onNavigate(Screen.FollowingScreen.route + "/${profile.userId}")
                        },
                        onFollowerClick = {
                            onNavigate(Screen.FollowerScreen.route + "/${profile.userId}")
                        },
                        onFollowClick = {
                            viewModel.onEvent(ProfileEvent.ToggleFollowStateForUser(profile.userId))
                        },
                        onMessageClick = {
                            val encodedProfilePictureUrl = Base64.encodeToString(profile.profilePictureUrl.encodeToByteArray(), 0)
                            onNavigate(
                                Screen.MessageScreen.route + "/${profile.userId}/${profile.username}/${encodedProfilePictureUrl}"
                            )
                        }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
            if(pagingPostState.items.isEmpty() && !pagingPostState.isFirstLoading && !pagingPostState.isNextLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = SpaceLarge),
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
                            text = stringResource(R.string.no_posts_for_profile),
                            style = Typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
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
                        onLikeClick = {
                            viewModel.onEvent(ProfileEvent.LikedPost(post.id))
                        },
                        onCommentClick = {
                            viewModel.onEvent(ProfileEvent.SelectPostId(post.id))
                            viewModel.onEvent(ProfileEvent.ShowBottomSheet)
                            viewModel.onEvent(ProfileEvent.LoadComments)
                        },
                        onShareClick = {
                            context.sendSharePostIntent(post.id)
                        },
                        onSaveClick = {
                            viewModel.onEvent(ProfileEvent.SavePost(post.id))
                        },
                        onLikedByClick = {
                            onNavigate(Screen.PersonListScreen.route + "/${post.id}")
                        },
                        onMoreItemClick = {
                            viewModel.onEvent(ProfileEvent.SelectPostId(post.id))
                            viewModel.onEvent(ProfileEvent.SelectPostUsername(post.username, post.isOwnPost))
                            viewModel.onEvent(ProfileEvent.ShowDeleteSheet)
                        },
                        isDescriptionVisible = state.isDescriptionVisible[post.id] ?: false,
                        onDescriptionToggle = {
                            viewModel.onEvent(ProfileEvent.OnDescriptionToggle(post.id))
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
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            state.profile?.let { profile ->
                BannerSection(
                    modifier = Modifier
                        .height(
                            (bannerHeight * toolbarState.expandedRatio).coerceIn(
                                minimumValue = toolbarHeightCollapsed,
                                maximumValue = bannerHeight
                            )
                        ),
                    leftIconModifier = Modifier
                        .graphicsLayer {
                            translationY = (1f - toolbarState.expandedRatio) *
                                    -iconCollapsedOffsetY.toPx()
                            translationX = (1f - toolbarState.expandedRatio) *
                                    iconHorizontalCenterLength
                        },
                    rightIconModifier = Modifier
                        .graphicsLayer {
                            translationY = (1f - toolbarState.expandedRatio) *
                                    -iconCollapsedOffsetY.toPx()
                            translationX = (1f - toolbarState.expandedRatio) *
                                    -iconHorizontalCenterLength
                        },
                    imageLoader = imageLoader,
                    topSkills = profile.topSkills,
                    shouldShowGitHub = !profile.gitHubUrl.isNullOrBlank(),
                    shouldShowInstagram = !profile.instagramUrl.isNullOrBlank(),
                    shouldShowLinkedIn = !profile.linkedInUrl.isNullOrBlank(),
                    bannerUrl = profile.bannerUrl,
                    isOwnProfile = profile.isOwnProfile,
                    expanded = state.isDropdownMenuVisible,
                    onShowDropDownMenu = {
                        viewModel.onEvent(ProfileEvent.ShowDropDownMenu)
                    },
                    onDismissDropdownMenu = {
                        viewModel.onEvent(ProfileEvent.DisMissDropDownMenu)
                    },
                    onGitHubClick = {
                        context.openUrlInBrowser(profile.gitHubUrl ?: return@BannerSection)
                    },
                    onInstagramClick = {
                        context.openUrlInBrowser(profile.instagramUrl ?: return@BannerSection)
                    },
                    onLinkedInClick = {
                        context.openUrlInBrowser(profile.linkedInUrl ?: return@BannerSection)
                    },
                    onEditClick = {
                        onNavigate(Screen.EditProfileScreen.route + "/${profile.userId}")
                    },
                    onSavedClick = {
                        onNavigate(Screen.SavedPostScreen.route)
                    },
                    onLogoutClick = {
                        viewModel.onEvent(ProfileEvent.ShowLogoutDialog)
                    },
                    onNavigateUp = {
                        onNavigateUp()
                    }
                )
                AsyncImage(
                    model = profile.profilePictureUrl,
                    contentDescription = stringResource(R.string.profile),
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .graphicsLayer {
                            translationY = -profilePictureSize.toPx() / 2f -
                                    (1f - toolbarState.expandedRatio) * imageCollapsedOffsetY.toPx()
                            transformOrigin = TransformOrigin(
                                pivotFractionX = 0.5f,
                                pivotFractionY = 0f
                            )
                            val scale = 0.5f + toolbarState.expandedRatio * 0.5f
                            scaleX = scale
                            scaleY = scale
                        }
                        .size(ProfilePictureSizeLarge)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                )
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
        if(state.isDeleteSheetVisible) {
            StandardBottomSheet(
                title = state.selectedPostUsername.toString(),
                bottomSheetState = bottomSheetState,
                showDownloadOption = true,
                showDeleteOption = state.isOwnPost == true,
                onDismissRequest = {
                    viewModel.onEvent(ProfileEvent.DismissDeleteSheet)
                },
                onDownloadClick = {
                    viewModel.onEvent(ProfileEvent.DownloadPost)
                    viewModel.onEvent(ProfileEvent.DismissDeleteSheet)
                },
                onDeleteClick = {
                    viewModel.onEvent(ProfileEvent.DeletePost)
                    viewModel.onEvent(ProfileEvent.DismissDeleteSheet)
                },
                onCancelClick = {
                    viewModel.onEvent(ProfileEvent.DismissDeleteSheet)
                }
            )
        }
        if(state.isLogoutDialogVisible) {
            Dialog(
                onDismissRequest = {
                viewModel.onEvent(ProfileEvent.DismissLogoutDialog)
            }) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(SpaceMedium)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out),
                        style = Typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    Text(
                        text = stringResource(id = R.string.log_out_of_your_account),
                        style = Typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(SpaceLarge))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.align(End)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = Typography.labelMedium,
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            viewModel.onEvent(ProfileEvent.DismissLogoutDialog)
                                        }
                                    )
                                }
                        )
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.log_out),
                            style = Typography.labelMedium.withColor(Color.Red),
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            viewModel.onEvent(ProfileEvent.DismissLogoutDialog)
                                            viewModel.onEvent(ProfileEvent.Logout)
                                            onLogout()
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }
        ConnectivityBanner(
            networkState = networkState,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        if(state.isLoading || pagingPostState.isFirstLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
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
    viewModel: ProfileViewModel,
    focusRequester: FocusRequester,
    bottomSheetState: SheetState,
    commentTextFieldState: StandardTextFieldState,
    state: ProfileState
) {
    if(state.isBottomSheetVisible) {
        PaginatedBottomSheet(
            title = stringResource(R.string.comments),
            bottomSheetState = bottomSheetState,
            onDismissBottomSheet = {
                viewModel.onEvent(ProfileEvent.DismissBottomSheet)
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
                viewModel.onEvent(ProfileEvent.ChangeCommentFilter(filterType))
            },
            isDropdownMenuExpanded = state.isFilterMenuVisible,
            onShowDropDownMenu = {
                viewModel.onEvent(ProfileEvent.ShowFilterMenu)
            },
            onDismissDropdownMenu = {
                viewModel.onEvent(ProfileEvent.DismissFilterMenu)
            },
            keyExtractor = { comment ->
                comment.id
            },
            textFieldState = commentTextFieldState,
            onValueChange = {
                viewModel.onEvent(ProfileEvent.EnteredComment(it))
            },
            onSend = {
                viewModel.onEvent(ProfileEvent.Comment)
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
                    viewModel.onEvent(ProfileEvent.LikedComment(comment.id))
                },
                onLikedByClick = {
                    viewModel.onEvent(ProfileEvent.NavigatedToPersonListScreen)
                    onNavigate(Screen.PersonListScreen.route + "/${comment.id}")
                },
                onLongPress = {
                    viewModel.onEvent(ProfileEvent.SelectComment(comment.id))
                },
                onDeleteClick = {
                    viewModel.onEvent(ProfileEvent.DeleteComment)
                }
            )
        }
    }
}