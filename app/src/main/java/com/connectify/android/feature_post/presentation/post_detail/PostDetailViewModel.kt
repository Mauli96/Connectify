package com.connectify.android.feature_post.presentation.post_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.R
import com.connectify.android.core.data.connectivity.ConnectivityObserver
import com.connectify.android.core.domain.models.Comment
import com.connectify.android.core.domain.states.NetworkConnectionState
import com.connectify.android.core.domain.states.PagingState
import com.connectify.android.core.domain.states.StandardTextFieldState
import com.connectify.android.core.domain.use_case.GetOwnProfilePictureUseCase
import com.connectify.android.core.domain.use_case.GetPostDownloadUrlUseCase
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.DefaultPaginator
import com.connectify.android.core.util.ParentType
import com.connectify.android.core.util.PostDownloader
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_post.domain.use_case.PostUseCases
import com.connectify.android.feature_post.presentation.util.CommentError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
class PostDetailViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val getPostDownloadUrlUseCase: GetPostDownloadUrlUseCase,
    private val postDownloader: PostDownloader,
    private val connectivityObserver: ConnectivityObserver,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailState())
    val state = _state.asStateFlow()

    private val _pagingCommentState = MutableStateFlow<PagingState<Comment>>(PagingState())
    val pagingCommentState = _pagingCommentState
        .onStart { loadInitialComments() }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    private val _profilePictureState = MutableStateFlow("")
    val profilePictureState = _profilePictureState
        .onStart { getOwnProfilePicture() }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _commentTextFieldState = MutableStateFlow(StandardTextFieldState(error = CommentError.FieldEmpty))
    val commentTextFieldState = _commentTextFieldState.asStateFlow()

    private val _commentState = MutableStateFlow(CommentState())
    val commentState = _commentState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        savedStateHandle.get<String>("postId")?.let { postId ->
            loadPostDetails(postId)
        }
    }

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
            val filterType = commentState.value.commentFilter
            savedStateHandle.get<String>("postId")?.let { postId ->
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

    fun onEvent(event: PostDetailEvent) {
        when(event) {
            is PostDetailEvent.OnLikePost -> {
                val isLiked = state.value.post?.isLiked == true
                toggleLikeForParent(
                    parentId = state.value.post?.id ?: return,
                    parentType = ParentType.Post.type,
                    isLiked = isLiked
                )
            }
            is PostDetailEvent.OnComment -> {
                createComment(
                    postId = savedStateHandle.get<String>("postId") ?: "",
                    comment = commentTextFieldState.value.text
                )
            }
            is PostDetailEvent.OnLikeComment -> {
                val isLiked = pagingCommentState.value.items.find {
                    it.id == event.commentId
                }?.isLiked == true
                toggleLikeForParent(
                    parentId = event.commentId,
                    parentType = ParentType.Comment.type,
                    isLiked = isLiked
                )
            }
            is PostDetailEvent.OnEnteredComment -> {
                _commentTextFieldState.update {
                    it.copy(
                        text = event.comment,
                        error = if(event.comment.isBlank()) CommentError.FieldEmpty else null
                    )
                }
            }
            is PostDetailEvent.OnSavePost -> {
                val isSaved = state.value.post?.isSaved == true
                toggleSavePost(
                    parentId = event.postId,
                    isSaved = isSaved
                )
            }
            is PostDetailEvent.OnSelectPostUsername -> {
                _state.update {
                    it.copy(
                        selectedPostUsername = event.postUsername,
                        isOwnPost = event.isOwnPost
                    )
                }
            }
            is PostDetailEvent.OnDownloadPost -> {
                getPostDownloadUrl(savedStateHandle.get<String>("postId") ?: "")
            }
            is PostDetailEvent.OnSelectComment -> {
                _state.update {
                    it.copy(
                        selectedCommentId = event.commentId
                    )
                }
            }
            is PostDetailEvent.OnChangeCommentFilter -> {
                _commentState.update {
                    it.copy(
                        commentFilter = event.filterType
                    )
                }
                loadInitialComments()
            }
            is PostDetailEvent.OnDeletePost -> {
                deletePost(savedStateHandle.get<String>("postId") ?: "")
            }
            is PostDetailEvent.OnDeleteComment -> {
                _state.value.selectedCommentId?.let { commentId ->
                    deleteComment(commentId)
                }
            }
            is PostDetailEvent.OnDescriptionToggle -> {
                _state.update {
                    it.copy(
                        isDescriptionVisible = !state.value.isDescriptionVisible
                    )
                }
            }
            is PostDetailEvent.OnShowBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = true
                    )
                }
            }
            is PostDetailEvent.OnDismissBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = false
                    )
                }
            }
            is PostDetailEvent.OnShowDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownExpanded = true
                    )
                }
            }
            is PostDetailEvent.OnDismissDropDownMenu -> {
                _state.update {
                    it.copy(
                        isDropdownExpanded = false
                    )
                }
            }
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

    private fun getOwnProfilePicture() {
        viewModelScope.launch {
            val result = getOwnProfilePictureUseCase()
            when(result) {
                is Resource.Success -> {
                    _profilePictureState.value = result.data.toString()
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

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            val result = postUseCases.deletePost(postId)
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            post = null
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

    private fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val result = postUseCases.deleteComment(commentId)
            when(result) {
                is Resource.Success -> {
                    _pagingCommentState.update {
                        it.copy(
                            items = pagingCommentState.value.items.filter { comment ->
                                comment.id != commentId
                            }
                        )
                    }
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_comment
                        ))
                    )
                    _state.update {
                        it.copy(
                            post = it.post?.copy(
                                commentCount = (it.post.commentCount - 1).coerceAtLeast(0)
                            )
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

    private fun toggleSavePost(
        parentId: String,
        isSaved: Boolean
    ) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    post = state.value.post?.copy(
                        isSaved = !isSaved
                    )
                )
            }
            val result = postUseCases.toggleSavePost(
                postId = parentId,
                isSaved = isSaved
            )
            when(result) {
                is Resource.Success -> {
                    if(isSaved) {
                        _eventFlow.send(UiEvent.ShowSnackbar(
                            uiText = UiText.StringResource(R.string.successfully_unsaved_post)
                        ))
                    } else {
                        _eventFlow.send(UiEvent.ShowSnackbar(
                            uiText = UiText.StringResource(R.string.successfully_saved_post)
                        ))
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            post = state.value.post?.copy(
                                isSaved = !isSaved
                            )
                        )
                    }
                }
            }
        }
    }

    private fun toggleLikeForParent(
        parentId: String,
        parentType: Int,
        isLiked: Boolean
    ) {
        viewModelScope.launch {
            val currentLikeCount = state.value.post?.likeCount ?: 0
            when(parentType) {
                ParentType.Post.type -> {
                    val post = state.value.post
                    _state.update {
                        it.copy(
                            post = state.value.post?.copy(
                                isLiked = !isLiked,
                                likeCount = if(isLiked) {
                                    post?.likeCount?.minus(1) ?: 0
                                } else post?.likeCount?.plus(1) ?: 0
                            )
                        )
                    }
                }
                ParentType.Comment.type -> {
                    _pagingCommentState.update {
                        it.copy(
                            items = pagingCommentState.value.items.map { comment ->
                                if(comment.id == parentId) {
                                    comment.copy(
                                        isLiked = !isLiked,
                                        likeCount = if(isLiked) {
                                            comment.likeCount - 1
                                        } else comment.likeCount + 1
                                    )
                                } else comment
                            }
                        )
                    }
                }
            }
            val result = postUseCases.toggleLikeForParent(
                parentId = parentId,
                parentType = parentType,
                isLiked = isLiked
            )
            when(result) {
                is Resource.Success -> Unit
                is Resource.Error -> {
                    when(parentType) {
                        ParentType.Post.type -> {
                            _state.update {
                                it.copy(
                                    post = state.value.post?.copy(
                                        isLiked = isLiked,
                                        likeCount = currentLikeCount
                                    )
                                )
                            }
                        }
                        ParentType.Comment.type -> {
                            _pagingCommentState.update {
                                it.copy(
                                    items = pagingCommentState.value.items.map { comment ->
                                        if(comment.id == parentId) {
                                            comment.copy(
                                                isLiked = isLiked,
                                                likeCount = if(comment.isLiked) {
                                                    comment.likeCount - 1
                                                } else comment.likeCount + 1
                                            )
                                        } else comment
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createComment(
        postId: String,
        comment: String
    ) {
        viewModelScope.launch {
            _commentState.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = postUseCases.createComment(
                postId = postId,
                comment = comment
            )
            when(result) {
                is Resource.Success -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = UiText.StringResource(R.string.comment_posted)
                    ))
                    _commentState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _commentTextFieldState.update {
                        it.copy(
                            text = "",
                            error = CommentError.FieldEmpty
                        )
                    }
                    loadInitialComments()

                    _state.update {
                        it.copy(
                            post = it.post?.copy(
                                commentCount = it.post.commentCount + 1
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    _commentState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadPostDetails(postId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingPost = true
                )
            }
            val result = postUseCases.getPostDetails(postId)
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            post = result.data,
                            isLoadingPost = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoadingPost = false
                        )
                    }
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