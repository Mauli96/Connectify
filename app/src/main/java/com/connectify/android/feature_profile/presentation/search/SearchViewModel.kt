package com.connectify.android.feature_profile.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.core.data.connectivity.ConnectivityObserver
import com.connectify.android.core.domain.states.NetworkConnectionState
import com.connectify.android.core.domain.states.StandardTextFieldState
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_profile.domain.use_case.ProfileUseCases
import com.connectify.android.feature_profile.domain.util.ProfileConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _searchFieldState = MutableStateFlow(StandardTextFieldState())
    val searchFieldState = _searchFieldState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    private var searchJob: Job? = null

    fun onEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.OnQuery -> {
                searchUser(event.query)
            }
            is SearchEvent.OnToggleFollow -> {
                toggleFollowStateForUser(event.userId)
            }
            is SearchEvent.OnToggleSearch -> {
                searchUser(event.query)
            }
        }
    }

    private fun toggleFollowStateForUser(userId: String) {
        viewModelScope.launch {
            val isFollowing = state.value.userItems.find {
                it.userId == userId
            }?.isFollowing == true
            _state.update {
                it.copy(
                    userItems = state.value.userItems.map { userItem ->
                        if(userItem.userId == userId) {
                            userItem.copy(isFollowing = !userItem.isFollowing)
                        } else userItem
                    }
                )
            }
            val result = profileUseCases.toggleFollowStateForUser(
                userId = userId,
                isFollowing = isFollowing
            )
            when(result) {
                is Resource.Success -> Unit
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            userItems = state.value.userItems.map { userItem ->
                                if(userItem.userId == userId) {
                                    userItem.copy(isFollowing = isFollowing)
                                } else userItem
                            }
                        )
                    }
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    private fun searchUser(query: String) {
        _searchFieldState.update {
            it.copy(
                text = query
            )
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            delay(ProfileConstants.SEARCH_DELAY)
            val result = profileUseCases.searchUser(query)
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            userItems = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(result.uiText ?: UiText.unknownError())
                    )
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}