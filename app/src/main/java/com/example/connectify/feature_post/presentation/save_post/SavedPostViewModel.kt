package com.example.connectify.feature_post.presentation.save_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.feature_post.domain.use_case.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPostViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _pagingPostState = MutableStateFlow<PagingState<Post>>(PagingState())
    val pagingPostState = _pagingPostState
        .onStart { loadInitialPosts() }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

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
            _eventFlow.send(UiEvent.ShowSnackbar(uiText))
        }
    )

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
}