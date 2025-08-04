package com.connectify.android.feature_post.presentation.post_detail

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
import com.connectify.android.R
import com.connectify.android.core.presentation.components.Comment
import com.connectify.android.core.presentation.components.CommentFilterDropdown
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.Post
import com.connectify.android.core.presentation.components.SendTextField
import com.connectify.android.core.presentation.components.StandardBottomSheet
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.ui.theme.DarkGray
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.presentation.util.showKeyboard
import com.connectify.android.core.util.Constants
import com.connectify.android.core.util.Screen
import com.connectify.android.core.util.sendSharePostIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingCommentState by viewModel.pagingCommentState.collectAsStateWithLifecycle()
    val commentTextFieldState by  viewModel.commentTextFieldState.collectAsStateWithLifecycle()
    val commentState by viewModel.commentState.collectAsStateWithLifecycle()
    val profilePictureState by viewModel.profilePictureState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                title = {
                    Text(
                        text = stringResource(id = R.string.post),
                        style = Typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                showBackArrow = true,
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Box(
                            modifier = Modifier.fillMaxSize()
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
                                            println("The post useId : ${post.userId}")
                                        },
                                        onLikeClick = {
                                            viewModel.onEvent(PostDetailEvent.OnLikePost)
                                        },
                                        onCommentClick = {
                                            context.showKeyboard()
                                            focusRequester.requestFocus()
                                        },
                                        onShareClick = {
                                            context.sendSharePostIntent(post.id)
                                        },
                                        onSaveClick = {
                                            viewModel.onEvent(PostDetailEvent.OnSavePost(post.id))
                                        },
                                        onLikedByClick = {
                                            onNavigate(Screen.PersonListScreen.route + "/${post.id}")
                                        },
                                        onMoreItemClick = {
                                            viewModel.onEvent(PostDetailEvent.OnSelectPostUsername(post.username, post.isOwnPost))
                                            viewModel.onEvent(PostDetailEvent.OnShowBottomSheet)
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
                                        viewModel.onEvent(PostDetailEvent.OnDismissBottomSheet)
                                    },
                                    onDownloadClick = {
                                        viewModel.onEvent(PostDetailEvent.OnDownloadPost)
                                        viewModel.onEvent(PostDetailEvent.OnDismissBottomSheet)
                                    },
                                    onDeleteClick = {
                                        viewModel.onEvent(PostDetailEvent.OnDeletePost)
                                        viewModel.onEvent(PostDetailEvent.OnDismissBottomSheet)
                                    },
                                    onCancelClick = {
                                        viewModel.onEvent(PostDetailEvent.OnDismissBottomSheet)
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
                                viewModel.onEvent(PostDetailEvent.OnShowDropDownMenu)
                            },
                            onDismissDropdownMenu = {
                                viewModel.onEvent(PostDetailEvent.OnDismissDropDownMenu)
                            },
                            selectedFilter = commentState.commentFilter,
                            onFilterSelected = { filterType ->
                                viewModel.onEvent(PostDetailEvent.OnChangeCommentFilter(filterType))
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
                        modifier = Modifier.fillMaxWidth(),
                        comment = comment,
                        context = context,
                        imageLoader = imageLoader,
                        onLikeClick = {
                            viewModel.onEvent(PostDetailEvent.OnLikeComment(comment.id))
                        },
                        onLikedByClick = {
                            onNavigate(Screen.PersonListScreen.route + "/${comment.id}")
                        },
                        onLongPress = {
                            viewModel.onEvent(PostDetailEvent.OnSelectComment(comment.id))
                        },
                        onDeleteClick = {
                            viewModel.onEvent(PostDetailEvent.OnDeleteComment)
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
                imageLoader = imageLoader,
                onValueChange = {
                    viewModel.onEvent(PostDetailEvent.OnEnteredComment(it))
                },
                onSend = {
                    viewModel.onEvent(PostDetailEvent.OnComment)
                },
                ownProfilePicture = profilePictureState,
                hint = stringResource(id = R.string.enter_a_comment),
                isUploading = commentState.isLoading,
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