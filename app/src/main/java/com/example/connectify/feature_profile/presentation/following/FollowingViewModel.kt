package com.example.connectify.feature_profile.presentation.following

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
class FollowingViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val toggleFollowStateForUserUseCase: ToggleFollowStateForUserUseCase,
    private val getOwnUserId: GetOwnUserIdUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(FollowingState())
    val state = _state.asStateFlow()

    private val _ownUserId = MutableStateFlow("")
    val ownUserId = _ownUserId.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            getFollowsByUser(userId)
            _ownUserId.value = getOwnUserId()
        }
    }

    fun onEvent(event: FollowingEvent) {
        when(event) {
            is FollowingEvent.OnToggleFollowStateForUser -> {
                toggleFollowStateForUser(event.userId)
            }
        }
    }

    private fun toggleFollowStateForUser(userId: String) {
        viewModelScope.launch {
            val isFollowing = state.value.users.find {
                it.userId == userId
            }?.isFollowing == true

            _state.update {
                it.copy(
                    users = state.value.users.map { userItem ->
                        if(userItem.userId == userId) {
                            userItem.copy(
                                isFollowing = !userItem.isFollowing
                            )
                        } else userItem
                    }
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
                            users = state.value.users.map { userItem ->
                                if(userItem.userId == userId) {
                                    userItem.copy(
                                        isFollowing = isFollowing
                                    )
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

    private fun getFollowsByUser(userId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = profileUseCases.getFollowsByUser(userId)
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            users = result.data ?: emptyList(),
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
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(result.uiText ?: UiText.unknownError())
                    )
                }
            }
        }
    }
}