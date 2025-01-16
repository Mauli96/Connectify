package com.example.connectify.feature_profile.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.domain.use_case.GetOwnProfilePictureUseCase
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.GetPostDownloadUrlUseCase
import com.example.connectify.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.CommentLiker
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.ParentType
import com.example.connectify.core.util.PostDownloader
import com.example.connectify.core.util.PostLiker
import com.example.connectify.core.util.PostSaver
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import com.example.connectify.feature_post.presentation.util.CommentError
import com.example.connectify.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val postLiker: PostLiker,
    private val postSaver: PostSaver,
    private val commentLiker: CommentLiker,
    private val postUseCases: PostUseCases,
    private val getOwnUserId: GetOwnUserIdUseCase,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val toggleFollowStateForUserUseCase: ToggleFollowStateForUserUseCase,
    private val getPostDownloadUrlUseCase: GetPostDownloadUrlUseCase,
    private val postDownloader: PostDownloader,
    private val connectivityObserver: ConnectivityObserver,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _pagingPostState = MutableStateFlow<PagingState<Post>>(PagingState())
    val pagingPostState = _pagingPostState
        .onStart { loadInitialPosts() }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    private val _pagingCommentState = MutableStateFlow<PagingState<Comment>>(PagingState())
    val pagingCommentState = _pagingCommentState.asStateFlow()

    private val _commentTextFieldState = MutableStateFlow(StandardTextFieldState(error = CommentError.FieldEmpty))
    val commentTextFieldState = _commentTextFieldState.asStateFlow()

    private val _toolbarState = MutableStateFlow(ProfileToolbarState())
    val toolbarState = _toolbarState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        val userId = savedStateHandle.get<String>("userId") ?: getOwnUserId()
        getProfile(userId)
    }

    private val postPaginator = DefaultPaginator(
        onFirstLoadUpdated = { isFirstLoading ->
            _pagingPostState.update {
                it.copy(
                    isFirstLoading = isFirstLoading
                )
            }
        },
        onNextLoadUpdated = { isNextLoading ->
            _pagingPostState.update {
                it.copy(
                    isNextLoading = isNextLoading
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
            _pagingPostState.update {
                it.copy(
                    items = if(firstPage) posts else pagingPostState.value.items + posts,
                    endReached = posts.isEmpty()
                )
            }
        },
        onError = { uiText ->
            _eventFlow.send(UiEvent.ShowSnackbar(uiText))
        }
    )

    private val commentPaginator = DefaultPaginator(
        onFirstLoadUpdated = { isFirstLoading ->
            _pagingCommentState.update {
                it.copy(
                    isFirstLoading = isFirstLoading
                )
            }
        },
        onNextLoadUpdated = { isNextLoading ->
            _pagingCommentState.update {
                it.copy(
                    isNextLoading = isNextLoading
                )
            }
        },
        onRequest = { page ->
            val filterType = state.value.commentFilter
            state.value.selectedPostId?.let { postId ->
                postUseCases.getCommentsForPost(
                    postId = postId,
                    filterType = filterType,
                    page = page
                )
            } ?: Resource.Error(UiText.unknownError())
        },
        onSuccess = { comments, firstPage ->
            _pagingCommentState.update {
                it.copy(
                    items = if(firstPage) comments else pagingCommentState.value.items + comments,
                    endReached = comments.isEmpty()
                )
            }
        },
        onError = { uiText ->
            _eventFlow.send(UiEvent.ShowSnackbar(uiText))
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

    fun onEvent(event: ProfileEvent) {
        when(event) {
            is ProfileEvent.ToggleFollowStateForUser -> {
                toggleFollowStateForUser(event.userId)
            }
            is ProfileEvent.LikedPost -> {
                toggleLikeForParent(
                    parentId = event.postId,
                    parentType = ParentType.Post.type
                )
            }
            is ProfileEvent.LikedComment -> {
                toggleLikeForParent(
                    parentId = event.commentId,
                    parentType = ParentType.Comment.type
                )
            }
            is ProfileEvent.LoadComments -> {
                loadInitialComments()
                getOwnProfilePicture()
            }
            is ProfileEvent.Comment -> {
                state.value.selectedPostId?.let { postId ->
                    createComment(
                        postId = postId,
                        comment = commentTextFieldState.value.text
                    )
                }
            }
            is ProfileEvent.EnteredComment -> {
                _commentTextFieldState.update {
                    it.copy(
                        text = event.comment,
                        error = if(event.comment.isBlank()) CommentError.FieldEmpty else null
                    )
                }
            }
            is ProfileEvent.SavePost -> {
                toggleSavePost(parentId = event.postId)
            }
            is ProfileEvent.SelectPostId -> {
                _state.update {
                    it.copy(
                        selectedPostId = event.postId
                    )
                }
            }
            is ProfileEvent.SelectPostUsername -> {
                _state.update {
                    it.copy(
                        selectedPostUsername = event.postUsername,
                        isOwnPost = event.isOwnPost
                    )
                }
            }
            is ProfileEvent.DeletePost -> {
                _state.value.selectedPostId?.let { postId ->
                    deletePost(postId)
                }
            }
            is ProfileEvent.DownloadPost -> {
                _state.value.selectedPostId?.let { postId ->
                    getPostDownloadUrl(postId)
                }
            }
            is ProfileEvent.SelectComment -> {
                _state.update {
                    it.copy(
                        selectedCommentId = event.commentId
                    )
                }
            }
            is ProfileEvent.DeleteComment -> {
                state.value.selectedCommentId?.let { commentId ->
                    deleteComment(commentId)
                }
            }
            is ProfileEvent.ChangeCommentFilter -> {
                _state.update {
                    it.copy(
                        commentFilter = event.filterType
                    )
                }
                loadInitialComments()
            }
            is ProfileEvent.ShowDeleteSheet -> {
                _state.update {
                    it.copy(
                        isDeleteSheetVisible = true
                    )
                }
            }
            is ProfileEvent.DismissDeleteSheet -> {
                _state.update {
                    it.copy(
                        isDeleteSheetVisible = false
                    )
                }
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
            is ProfileEvent.ShowFilterMenu -> {
                _state.update {
                    it.copy(
                        isFilterMenuVisible = true
                    )
                }
            }
            is ProfileEvent.DismissFilterMenu -> {
                _state.update {
                    it.copy(
                        isFilterMenuVisible = false
                    )
                }
            }
            is ProfileEvent.ShowDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownMenuVisible = true
                    )
                }
            }
            is ProfileEvent.DisMissDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownMenuVisible = false
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
            is ProfileEvent.OnDescriptionToggle -> {
                _state.update { currentState ->
                    val currentVisibility = currentState.isDescriptionVisible[event.postId] ?: false
                    currentState.copy(
                        isDescriptionVisible = currentState.isDescriptionVisible.toMutableMap().apply {
                            this[event.postId] = !currentVisibility
                        }
                    )
                }
            }
            is ProfileEvent.NavigatedToPersonListScreen -> {
                _state.update {
                    it.copy(
                        isNavigatedToPersonListScreen = true
                    )
                }
                resetHasNavigatedWithDelay()
            }
            is ProfileEvent.Logout -> {
                profileUseCases.logout()
            }
        }
    }

    private fun loadInitialPosts() {
        viewModelScope.launch {
            postPaginator.loadFirstItems()
        }
    }

    fun loadNextPosts() {
        viewModelScope.launch {
            postPaginator.loadNextItems()
        }
    }

    private fun loadInitialComments() {
        viewModelScope.launch {
            commentPaginator.loadFirstItems()
        }
    }

    fun loadNextComments() {
        viewModelScope.launch {
            commentPaginator.loadNextItems()
        }
    }

    private fun resetHasNavigatedWithDelay() {
        viewModelScope.launch {
            delay(500)
            _state.update {
                it.copy(
                    isNavigatedToPersonListScreen = false
                )
            }
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
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    private fun getProfile(userId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = profileUseCases.getProfile(userId = userId)
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
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            val result = postUseCases.deletePost(postId)
            when(result) {
                is Resource.Success -> {
                    _pagingPostState.update {
                        it.copy(
                            items = pagingPostState.value.items.filter { post ->
                                post.id != postId
                            }
                        )
                    }
                    _state.update {
                        it.copy(
                            selectedPostId = null
                        )
                    }
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_post
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }

    private fun getPostDownloadUrl(postId: String) {
        viewModelScope.launch {
            val result = getPostDownloadUrlUseCase(postId)
            when(result) {
                is Resource.Success -> {
                    postDownloader.downloadFile(result.data.toString())
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_downloaded_post
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }

    private fun getOwnProfilePicture() {
        viewModelScope.launch {
            val result = getOwnProfilePictureUseCase()
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            profilePicture = result.data.toString()
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }

    private fun toggleSavePost(parentId: String) {
        viewModelScope.launch {
            postSaver.toggleSave(
                posts = pagingPostState.value.items,
                parentId = parentId,
                onRequest = { isSaved ->
                    postUseCases.toggleSavePost(
                        postId = parentId,
                        isSaved = isSaved
                    )
                },
                onStateUpdated = { posts ->
                    _pagingPostState.update {
                        it.copy(
                            items = posts
                        )
                    }
                }
            )
            val post = pagingPostState.value.items.find { it.id == parentId }
            if(post != null) {
                if(post.isSaved) {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_saved_post
                        ))
                    )
                } else {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_unsaved_post
                        ))
                    )
                }
            }
        }
    }

    private fun toggleLikeForParent(
        parentId: String,
        parentType: Int,
    ) {
        viewModelScope.launch {
            when(parentType) {
                ParentType.Post.type -> {
                    postLiker.toggleLike(
                        posts = pagingPostState.value.items,
                        parentId = parentId,
                        onRequest = { isLiked ->
                            postUseCases.toggleLikeForParent(
                                parentId = parentId,
                                parentType = ParentType.Post.type,
                                isLiked = isLiked
                            )
                        },
                        onStateUpdated = { posts ->
                            _pagingPostState.update {
                                it.copy(
                                    items = posts
                                )
                            }
                        }
                    )
                }
                ParentType.Comment.type -> {
                    commentLiker.toggleLike(
                        comments = pagingCommentState.value.items,
                        parentId = parentId,
                        onRequest = { isLiked ->
                            postUseCases.toggleLikeForParent(
                                parentId = parentId,
                                parentType = ParentType.Comment.type,
                                isLiked = isLiked
                            )
                        },
                        onStateUpdated = { comments ->
                            _pagingCommentState.update {
                                it.copy(
                                    items = comments
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun createComment(
        postId: String,
        comment: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUploading = true
                )
            }
            val result = postUseCases.createComment(
                postId = postId,
                comment = comment
            )
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isUploading = false
                        )
                    }
                    _commentTextFieldState.update {
                        it.copy(
                            text = "",
                            error = CommentError.FieldEmpty
                        )
                    }
                    loadInitialComments()
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    _state.update {
                        it.copy(
                            isUploading = false
                        )
                    }
                }
            }
        }
    }

    private fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val result = postUseCases.deleteComment(commentId)
            when(result) {
                is Resource.Success -> {
                    _pagingCommentState.update {
                        it.copy(
                            items = _pagingCommentState.value.items.filter { comment ->
                                comment.id != commentId
                            }
                        )
                    }
                    _state.update {
                        it.copy(
                            selectedCommentId = null
                        )
                    }
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_comment
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }
}