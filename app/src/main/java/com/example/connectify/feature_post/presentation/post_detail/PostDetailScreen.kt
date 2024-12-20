package com.example.connectify.feature_post.presentation.post_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.presentation.components.Comment
import com.example.connectify.core.presentation.components.CommentFilterDropdown
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.Post
import com.example.connectify.core.presentation.components.SendTextField
import com.example.connectify.core.presentation.components.StandardBottomSheet
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.presentation.util.showKeyboard
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.sendSharePostIntent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingCommentState by viewModel.pagingCommentState.collectAsStateWithLifecycle()
    val commentTextFieldState by  viewModel.commentTextFieldState.collectAsStateWithLifecycle()
    val commentState by viewModel.commentState.collectAsStateWithLifecycle()
    val profilePictureState by viewModel.profilePictureState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val context = LocalContext.current

    val focusRequester = remember {
        FocusRequester()
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                title = {
                    Text(
                        text = stringResource(id = R.string.post),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                showBackArrow = true,
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .shadow(5.dp)
                                    .background(DarkGray)
                            ) {
                                state.post?.let { post ->
                                    Post(
                                        post = post,
                                        imageLoader = imageLoader,
                                        onUsernameClick = {
                                            onNavigate(Screen.ProfileScreen.route + "?userId=${post.userId}")
                                        },
                                        onLikeClick = {
                                            viewModel.onEvent(PostDetailEvent.LikePost)
                                        },
                                        onCommentClick = {
                                            context.showKeyboard()
                                            focusRequester.requestFocus()
                                        },
                                        onShareClick = {
                                            context.sendSharePostIntent(post.id)
                                        },
                                        onSaveClick = {
                                            viewModel.onEvent(PostDetailEvent.SavePost(post.id))
                                        },
                                        onLikedByClick = {
                                            onNavigate(Screen.PersonListScreen.route + "/${post.id}")
                                        },
                                        onMoreItemClick = {
                                            viewModel.onEvent(PostDetailEvent.SelectPostUsername(post.username, post.isOwnPost))
                                            viewModel.onEvent(PostDetailEvent.ShowBottomSheet)
                                        },
                                        isDescriptionVisible = state.isDescriptionVisible,
                                        onDescriptionToggle = {
                                            viewModel.onEvent(PostDetailEvent.OnDescriptionToggle)
                                        }
                                    )
                                }
                            }
                            if(state.isBottomSheetVisible) {
                                StandardBottomSheet(
                                    title = state.selectedPostUsername.toString(),
                                    showDownloadOption = true,
                                    showDeleteOption = state.isOwnPost == true,
                                    bottomSheetState = bottomSheetState,
                                    onDismissRequest = {
                                        viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                                    },
                                    onDownloadClick = {
                                        viewModel.onEvent(PostDetailEvent.DownloadPost)
                                        viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                                    },
                                    onDeleteClick = {
                                        viewModel.onEvent(PostDetailEvent.DeletePost)
                                        viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                                    },
                                    onCancelClick = {
                                        viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                                    }
                                )
                            }
                        }
                    }
                }
                if(pagingCommentState.items.isNotEmpty()) {
                    item {
                        CommentFilterDropdown(
                            expanded = state.isDropdownExpanded,
                            onShowDropDownMenu = {
                                viewModel.onEvent(PostDetailEvent.ShowDropDownMenu)
                            },
                            onDismissDropdownMenu = {
                                viewModel.onEvent(PostDetailEvent.DismissDropDownMenu)
                            },
                            selectedFilter = commentState.commentFilter,
                            onFilterSelected = { filterType ->
                                viewModel.onEvent(PostDetailEvent.ChangeCommentFilter(filterType))
                            }
                        )
                    }
                }
                items(
                    count = pagingCommentState.items.size,
                    key = { i ->
                        val comment = pagingCommentState.items[i]
                        comment.id
                    }
                ) { i ->
                    val comment = pagingCommentState.items[i]
                    if(i >= pagingCommentState.items.size - 1 && pagingCommentState.items.size >= Constants.DEFAULT_PAGE_SIZE
                        && !pagingCommentState.endReached && !pagingCommentState.isFirstLoading && !pagingCommentState.isNextLoading) {
                        viewModel.loadNextComments()
                    }
                    Comment(
                        modifier = Modifier
                            .fillMaxWidth(),
                        comment = comment,
                        context = context,
                        imageLoader = imageLoader,
                        onLikeClick = {
                            viewModel.onEvent(PostDetailEvent.LikeComment(comment.id))
                        },
                        onLikedByClick = {
                            onNavigate(Screen.PersonListScreen.route + "/${comment.id}")
                        },
                        onLongPress = {
                            viewModel.onEvent(PostDetailEvent.SelectComment(comment.id))
                        },
                        onDeleteClick = {
                            viewModel.onEvent(PostDetailEvent.DeleteComment)
                        }
                    )
                }
                if(pagingCommentState.isNextLoading) {
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
            SendTextField(
                state = commentTextFieldState,
                onValueChange = {
                    viewModel.onEvent(PostDetailEvent.EnteredComment(it))
                },
                onSend = {
                    viewModel.onEvent(PostDetailEvent.Comment)
                },
                ownProfilePicture = profilePictureState,
                hint = stringResource(id = R.string.enter_a_comment),
                isLoading = commentState.isLoading,
                focusRequester = focusRequester
            )
        }
        ConnectivityBanner(
            networkState = networkState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
        )
        if(state.isLoadingPost) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}