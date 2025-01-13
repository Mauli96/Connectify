package com.example.connectify.feature_post.presentation.main_feed

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
import com.example.connectify.core.domain.use_case.GetPostDownloadUrlUseCase
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val postLiker: PostLiker,
    private val postSaver: PostSaver,
    private val commentLiker: CommentLiker,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val getPostDownloadUrlUseCase: GetPostDownloadUrlUseCase,
    private val postDownloader: PostDownloader,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(MainFeedState())
    val state = _state.asStateFlow()

    private val _pagingPostState = MutableStateFlow<PagingState<Post>>(PagingState())
    val pagingPostState = _pagingPostState
        .onStart { loadInitialPosts() }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    private val _pagingCommentState = MutableStateFlow<PagingState<Comment>>(PagingState())
    val pagingCommentState = _pagingCommentState.asStateFlow()

    private val _commentTextFieldState = MutableStateFlow(StandardTextFieldState(error = CommentError.FieldEmpty))
    val commentTextFieldState = _commentTextFieldState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
            postUseCases.getPostsForFollows(page = page)
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
            _eventFlow.emit(UiEvent.ShowSnackbar(uiText))
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
            _eventFlow.emit(UiEvent.ShowSnackbar(uiText))
        }
    )

    fun onEvent(event: MainFeedEvent) {
        when(event) {
            is MainFeedEvent.OnLikedPost -> {
                toggleLikeForParent(
                    parentId = event.postId,
                    parentType = ParentType.Post.type
                )
            }
            is MainFeedEvent.OnLikedComment -> {
                toggleLikeForParent(
                    parentId = event.commentId,
                    parentType = ParentType.Comment.type
                )
            }
            is MainFeedEvent.OnLoadComments -> {
                loadInitialComments()
                getOwnProfilePicture()
            }
            is MainFeedEvent.OnComment -> {
                state.value.selectedPostId?.let { postId ->
                    createComment(
                        postId = postId,
                        comment = commentTextFieldState.value.text
                    )
                }
            }
            is MainFeedEvent.OnEnteredComment -> {
                _commentTextFieldState.update {
                    it.copy(
                        text = event.comment,
                        error = if(event.comment.isBlank()) CommentError.FieldEmpty else null
                    )
                }
            }
            is MainFeedEvent.OnSavePost -> {
                toggleSavePost(parentId = event.postId)
            }
            is MainFeedEvent.OnSelectPostId -> {
                _state.update {
                    it.copy(
                        selectedPostId = event.postId
                    )
                }
            }
            is MainFeedEvent.OnSelectPostUsername -> {
                _state.update {
                    it.copy(
                        selectedPostUsername = event.postUsername,
                        isOwnPost = event.isOwnPost
                    )
                }
            }
            is MainFeedEvent.OnDeletePost -> {
                _state.value.selectedPostId?.let { postId ->
                    deletePost(postId)
                }
            }
            is MainFeedEvent.OnDownloadPost -> {
                _state.value.selectedPostId?.let { postId ->
                    getPostDownloadUrl(postId)
                }
            }
            is MainFeedEvent.OnSelectComment -> {
                _state.update {
                    it.copy(
                        selectedCommentId = event.commentId
                    )
                }
            }
            is MainFeedEvent.OnDeleteComment -> {
                state.value.selectedCommentId?.let { commentId ->
                    deleteComment(commentId)
                }
            }
            is MainFeedEvent.OnNavigatedToSearchScreen -> {
                _state.update {
                    it.copy(
                        isNavigatedToSearchScreen = true
                    )
                }
                resetHasNavigatedWithDelay()
            }
            is MainFeedEvent.OnNavigatedToPersonListScreen -> {
                _state.update {
                    it.copy(
                        isNavigatedToPersonListScreen = true
                    )
                }
                resetHasNavigatedWithDelay()
            }
            is MainFeedEvent.OnChangeCommentFilter -> {
                _state.update {
                    it.copy(
                        commentFilter = event.filterType
                    )
                }
                loadInitialComments()
            }
            is MainFeedEvent.OnShowBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = true
                    )
                }
            }
            is MainFeedEvent.OnDismissBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = false
                    )
                }
            }
            is MainFeedEvent.OnShowCommentBottomSheet -> {
                _state.update {
                    it.copy(
                        isCommentBottomSheetVisible = true
                    )
                }
            }
            is MainFeedEvent.OnDismissCommentBottomSheet -> {
                _state.update {
                    it.copy(
                        isCommentBottomSheetVisible = false
                    )
                }
            }
            is MainFeedEvent.OnShowDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownMenuVisible = true
                    )
                }
            }
            is MainFeedEvent.OnDismissDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownMenuVisible = false
                    )
                }
            }
            is MainFeedEvent.OnDescriptionToggle -> {
                _state.update { currentState ->
                val currentVisibility = currentState.isDescriptionVisible[event.postId] ?: false
                    currentState.copy(
                        isDescriptionVisible = currentState.isDescriptionVisible.toMutableMap().apply {
                            this[event.postId] = !currentVisibility
                        }
                    )
                }
            }
        }
    }

    fun onRefreshPosts() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRefreshing = true)
            }
            loadInitialPosts()
            _state.update {
                it.copy(isRefreshing = false)
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
                    isNavigatedToSearchScreen = false,
                    isNavigatedToPersonListScreen = false
                )
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

    private fun getPostDownloadUrl(postId: String) {
        viewModelScope.launch {
            val result = getPostDownloadUrlUseCase(postId)
            when(result) {
                is Resource.Success -> {
                    postDownloader.downloadFile(result.data.toString())
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_downloaded_post
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
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
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
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_saved_post
                        ))
                    )
                } else {
                   _eventFlow.emit(
                       UiEvent.ShowSnackbar(UiText.StringResource(
                           R.string.successfully_unsaved_post
                       ))
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
                    _eventFlow.emit(UiEvent.ShowSnackbar(
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
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_comment
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
}