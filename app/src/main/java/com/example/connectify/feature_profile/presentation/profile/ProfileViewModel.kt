package com.example.connectify.feature_profile.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.example.connectify.core.presentation.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.ParentType
import com.example.connectify.core.util.PostLiker
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import com.example.connectify.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val postUseCases: PostUseCases,
    private val getOwnUserId: GetOwnUserIdUseCase,
    private val toggleFollowStateForUserUseCase: ToggleFollowStateForUserUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val postLiker: PostLiker
) : ViewModel() {

    private val _toolbarState = mutableStateOf(ProfileToolbarState())
    val toolbarState: State<ProfileToolbarState> = _toolbarState

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _pagingState = mutableStateOf<PagingState<Post>>(PagingState())
    val pagingState: State<PagingState<Post>> = _pagingState

    private val paginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingState.value = pagingState.value.copy(
                isLoading = isLoading
            )
        },
        onRequest = { page ->
            val userId = savedStateHandle.get<String>("userId") ?: getOwnUserId()
            profileUseCases.getPostsForProfile(
                userId = userId,
                page = page
            )
        },
        onSuccess = { posts ->
            _pagingState.value = pagingState.value.copy(
                items = pagingState.value.items + posts,
                endReached = posts.isEmpty(),
            )
        },
        onError = { uiText ->
            _eventFlow.emit(UiEvent.ShowSnackbar(uiText))
        }
    )

    fun setExpandedRatio(ratio: Float) {
        _toolbarState.value = _toolbarState.value.copy(expandedRatio = ratio)
    }

    fun setToolbarOffsetY(value: Float) {
        _toolbarState.value = _toolbarState.value.copy(toolbarOffsetY = value)
    }

    init {
        loadNextPosts()
    }

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.LikePost -> {
                viewModelScope.launch {
                    toggleLikeForParent(
                        parentId = event.postId
                    )
                }
            }
            is ProfileEvent.DeletePost -> {
                deletePost(event.postId)
            }
            is ProfileEvent.ToggleFollowStateForUser -> {
                toggleFollowStateForUser(event.userId)
            }
            is ProfileEvent.ShowBottomSheet -> {
                _state.value = state.value.copy(
                    isBottomSheetVisible = true
                )
            }
            is ProfileEvent.DismissBottomSheet -> {
                _state.value = state.value.copy(
                    isBottomSheetVisible = false
                )
            }
            is ProfileEvent.DeletePostId -> {
                _state.value = state.value.copy(
                    deletePostId = event.postId
                )
            }
            is ProfileEvent.ShowLogoutDialog -> {
                _state.value = state.value.copy(
                    isLogoutDialogVisible = true
                )
            }
            is ProfileEvent.DismissLogoutDialog -> {
                _state.value = state.value.copy(
                    isLogoutDialogVisible = false
                )
            }
            is ProfileEvent.Logout -> {
                profileUseCases.logout()
            }
        }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            val result = postUseCases.deletePost(postId)
            when(result) {
                is Resource.Success -> {
                    _pagingState.value = pagingState.value.copy(
                        items = pagingState.value.items.filter {
                            it.id != postId
                        }
                    )
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_post
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }

    private fun toggleLikeForParent(parentId: String) {
        viewModelScope.launch {
            postLiker.toggleLike(
                posts = pagingState.value.items,
                parentId = parentId,
                onRequest = { isLiked ->
                    postUseCases.toggleLikeForParent(
                        parentId = parentId,
                        parentType = ParentType.Post.type,
                        isLiked = isLiked
                    )
                },
                onStateUpdated = { posts ->
                    _pagingState.value = pagingState.value.copy(
                        items = posts,
                    )
                }
            )
        }
    }

    private fun toggleFollowStateForUser(userId: String) {
        viewModelScope.launch {
            val isFollowing = state.value.profile?.isFollowing == true
            val currentFollowerCount = state.value.profile?.followerCount ?: 0

            _state.value = _state.value.copy(
                profile = state.value.profile?.copy(
                    isFollowing = !isFollowing,
                    followerCount = if(isFollowing) {
                        currentFollowerCount - 1
                    } else {
                        currentFollowerCount + 1
                    }
                )
            )
            val result = toggleFollowStateForUserUseCase(
                userId = userId,
                isFollowing = isFollowing
            )
            when(result) {
                is Resource.Success -> Unit
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        profile = state.value.profile?.copy(
                            isFollowing = isFollowing,
                            followerCount = if(isFollowing) {
                                currentFollowerCount + 1
                            } else {
                                currentFollowerCount - 1
                            }
                        )
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    fun getProfile(userId: String?) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                isLoading = true
            )
            val result = profileUseCases.getProfile(userId = userId ?: getOwnUserId())
            when(result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        profile = result.data,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    fun loadNextPosts() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }
}