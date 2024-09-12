package com.example.connectify.feature_post.presentation.main_feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.ParentType
import com.example.connectify.core.util.PostLiker
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val postLiker: PostLiker
) : ViewModel() {

    private val _state = MutableStateFlow(MainFeedState())
    val state = _state.asStateFlow()

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
            postUseCases.getPostsForFollows(page = page)
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

    init {
        loadInitialFeed()
    }

    fun onEvent(event: MainFeedEvent) {
        when(event) {
            is MainFeedEvent.LikedPost -> {
                toggleLikeForParent(event.postId)
            }
            is MainFeedEvent.Navigated -> {
                _state.update {
                    it.copy(
                        hasNavigated = true
                    )
                }
                resetHasNavigatedWithDelay()
            }
        }
    }

    fun loadNextPosts() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    fun loadInitialFeed() {
        viewModelScope.launch {
            paginator.loadFirstItems()
        }
    }

    private fun resetHasNavigatedWithDelay() {
        viewModelScope.launch {
            delay(500)
            _state.update {
                it.copy(
                    hasNavigated = false
                )
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
                    _pagingState.update {
                        it.copy(
                            items = posts
                        )
                    }
                }
            )
        }
    }
}