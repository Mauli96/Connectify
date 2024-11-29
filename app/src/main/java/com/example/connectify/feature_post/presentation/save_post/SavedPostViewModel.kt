package com.example.connectify.feature_post.presentation.save_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import com.example.connectify.feature_post.presentation.main_feed.MainFeedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPostViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
) : ViewModel() {

    private val _pagingPostState = MutableStateFlow<PagingState<Post>>(PagingState())
    val pagingPostState = _pagingPostState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val postPaginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingPostState.update {
                it.copy(
                    isLoading = isLoading
                )
            }
        },
        onRequest = { page ->
            postUseCases.getSavedPosts(page = page)
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

    init {
        loadInitialPosts()
    }

    fun loadInitialPosts() {
        viewModelScope.launch {
            postPaginator.loadFirstItems()
        }
    }

    fun loadNextPosts() {
        viewModelScope.launch {
            postPaginator.loadNextItems()
        }
    }
}