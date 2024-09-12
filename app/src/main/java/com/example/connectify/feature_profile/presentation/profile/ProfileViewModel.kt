package com.example.connectify.feature_profile.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.ToggleFollowStateForUserUseCase
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _toolbarState = MutableStateFlow(ProfileToolbarState())
    val toolbarState = _toolbarState.asStateFlow()

    private val _pagingState = MutableStateFlow<PagingState<Post>>(PagingState())
    val pagingState = _pagingState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val paginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingState.update {
                it.copy(
                    isLoading = isLoading
                )
            }
        },
        onRequest = { page ->
            val userId = savedStateHandle.get<String>("userId") ?: getOwnUserId()
            profileUseCases.getPostsForProfile(
                userId = userId,
                page = page
            )
        },
        onSuccess = { posts, firstPage ->
            _pagingState.update {
                it.copy(
                    items = if(firstPage) posts else pagingState.value.items + posts,
                    endReached = posts.isEmpty()
                )
            }
        },
        onError = { uiText ->
            _eventFlow.emit(UiEvent.ShowSnackbar(uiText))
        }
    )

    fun setExpandedRatio(ratio: Float) {
        _toolbarState.update {
            it.copy(
                expandedRatio = ratio
            )
        }
    }

    fun setToolbarOffsetY(value: Float) {
        _toolbarState.update {
            it.copy(
                toolbarOffsetY = value
            )
        }
    }

    init {
        loadInitialPosts()
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
            is ProfileEvent.SelectPost -> {
                _state.update {
                    it.copy(
                        selectedPostId = event.postId
                    )
                }
            }
            is ProfileEvent.DeletePost -> {
                _state.value.selectedPostId?.let { postId ->
                    deletePost(postId)
                }
            }
            is ProfileEvent.ToggleFollowStateForUser -> {
                toggleFollowStateForUser(event.userId)
            }
            is ProfileEvent.ShowBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = true
                    )
                }
            }
            is ProfileEvent.DismissBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = false
                    )
                }
            }
            is ProfileEvent.ShowLogoutDialog -> {
                _state.update {
                    it.copy(
                        isLogoutDialogVisible = true
                    )
                }
            }
            is ProfileEvent.DismissLogoutDialog -> {
                _state.update {
                    it.copy(
                        isLogoutDialogVisible = false
                    )
                }
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
                    _pagingState.update {
                        it.copy(
                            items = pagingState.value.items.filter { post ->
                                post.id != postId
                            }
                        )
                    }
                    _state.update {
                        it.copy(
                            selectedPostId = null
                        )
                    }
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

    fun loadNextPosts() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun loadInitialPosts() {
        viewModelScope.launch {
            paginator.loadFirstItems()
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
                    _pagingState.update {
                        it.copy(
                            items = posts
                        )
                    }
                }
            )
        }
    }

    private fun toggleFollowStateForUser(userId: String) {
        viewModelScope.launch {
            val isFollowing = state.value.profile?.isFollowing == true
            val currentFollowerCount = state.value.profile?.followerCount ?: 0

            _state.update {
                it.copy(
                    profile = state.value.profile?.copy(
                        isFollowing = !isFollowing,
                        followerCount = if(isFollowing) {
                            currentFollowerCount - 1
                        } else {
                            currentFollowerCount + 1
                        }
                    )
                )
            }
            val result = toggleFollowStateForUserUseCase(
                userId = userId,
                isFollowing = isFollowing
            )
            when(result) {
                is Resource.Success -> Unit
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            profile = state.value.profile?.copy(
                                isFollowing = !isFollowing,
                                followerCount = if(isFollowing) {
                                    currentFollowerCount + 1
                                } else {
                                    currentFollowerCount - 1
                                }
                            )
                        )
                    }
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    fun getProfile(userId: String?) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = profileUseCases.getProfile(userId = userId ?: getOwnUserId())
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            profile = result.data,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }
}