package com.example.connectify.feature_post.presentation.post_detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.components.ActionRow
import com.example.connectify.core.presentation.components.SendTextField
import com.example.connectify.core.presentation.components.StandardBottomSheet
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.presentation.util.showKeyboard
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.sendSharePostIntent
import com.example.connectify.feature_post.presentation.post_detail.components.Comment
import com.example.connectify.feature_post.presentation.util.CommentFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    scaffoldState: ScaffoldState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: PostDetailViewModel = hiltViewModel(),
    shouldShowKeyboard: Boolean = false
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val commentTextFieldState by  viewModel.commentTextFieldState.collectAsStateWithLifecycle()
    val commentState by viewModel.commentState.collectAsStateWithLifecycle()
    val profilePictureState by viewModel.profilePictureState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()

    val focusRequester = remember {
        FocusRequester()
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        if(shouldShowKeyboard) {
            context.showKeyboard()
            focusRequester.requestFocus()
        }
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context)
                    )
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
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = post.imageUrl,
                                        imageLoader = imageLoader
                                    ),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = "Post image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(4f / 5f)
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = SpaceLarge,
                                            bottom = SpaceLarge,
                                            start = 10.dp,
                                            end = 10.dp
                                        )
                                ) {
                                    ActionRow(
                                        username = post.username,
                                        profilePictureUrl = post.profilePictureUrl,
                                        imageLoader = imageLoader,
                                        modifier = Modifier.fillMaxWidth(),
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
                                        isLiked = post.isLiked
                                    )
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Text(
                                        text = post.description,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                    Text(
                                        text = stringResource(
                                            id = R.string.liked_by_x_people,
                                            post.likeCount
                                        ),
                                        modifier = Modifier
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onTap = {
                                                        onNavigate(Screen.PersonListScreen.route + "/${post.id}")
                                                    }
                                                )
                                            },
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        if(state.isLoadingPost) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Center),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp,
                                trackColor = Color.White
                            )
                        }
                    }
                }
            }
            if(state.comments.isNotEmpty()) {
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
                items = state.comments,
                key = { comment ->
                    comment.id
                }
            ) { comment ->
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
                    onLongPress = { id ->
                        viewModel.onEvent(PostDetailEvent.SelectComment(id))
                        viewModel.onEvent(PostDetailEvent.ShowBottomSheet)
                    }
                )
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
        if(state.isBottomSheetVisible) {
            StandardBottomSheet(
                title = stringResource(id = R.string.delete_comment),
                bottomSheetState = bottomSheetState,
                onDismissRequest = {
                    viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                },
                onDeleteClick = {
                    viewModel.onEvent(PostDetailEvent.DeleteComment)
                    viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                },
                onCancelClick = {
                    viewModel.onEvent(PostDetailEvent.DismissBottomSheet)
                }
            )
        }
    }
}

@Composable
fun CommentFilterDropdown(
    expanded: Boolean,
    onShowDropDownMenu: () -> Unit,
    onDismissDropdownMenu: () -> Unit,
    selectedFilter: CommentFilter,
    onFilterSelected: (CommentFilter) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(
                horizontal = SpaceSmall,
                vertical = SpaceSmall
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onShowDropDownMenu()
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    id = when(selectedFilter) {
                        CommentFilter.MOST_RECENT -> R.string.most_recent_comments
                        CommentFilter.MOST_OLD -> R.string.most_old_commets
                        CommentFilter.MOST_POPULAR -> R.string.most_popular_comments
                    }
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Icon(
                painter = if(expanded) {
                    painterResource(R.drawable.drop_down_up_icon)
                } else {
                    painterResource(R.drawable.drop_down_icon)
                },
                contentDescription = stringResource(id = R.string.filter_comments),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(IconSizeSmall)
            )
        }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopStart)
                .clip(MaterialTheme.shapes.large)
                .shadow(30.dp)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onDismissDropdownMenu()
                },
                offset = DpOffset(0.dp, 26.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_recent_comments),
                            style = if(selectedFilter == CommentFilter.MOST_RECENT) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_RECENT)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_RECENT) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_old_commets),
                            style = if(selectedFilter == CommentFilter.MOST_OLD) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_OLD)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_OLD) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_popular_comments),
                            style = if(selectedFilter == CommentFilter.MOST_POPULAR) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_POPULAR)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_POPULAR) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}