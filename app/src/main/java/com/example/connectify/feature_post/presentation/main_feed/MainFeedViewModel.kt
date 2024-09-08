package com.example.connectify.feature_post.presentation.main_feed

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val postLiker: PostLiker
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _state = MutableStateFlow(MainFeedState())
    val state: StateFlow<MainFeedState> = _state

    private val _pagingState = mutableStateOf<PagingState<Post>>(PagingState())
    val pagingState: State<PagingState<Post>> = _pagingState

    private val paginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingState.value = pagingState.value.copy(
                isLoading = isLoading
            )
        },
        onRequest = { page ->
            postUseCases.getPostsForFollows(page = page)
        },
        onSuccess = { posts, firstPage ->
            _pagingState.value = pagingState.value.copy(
                items = if(firstPage) posts else pagingState.value.items + posts,
                endReached = posts.isEmpty(),
            )
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
                _state.value = _state.value.copy(
                    hasNavigated = true
                )
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
                        items = posts
                    )
                }
            )
        }
    }
}