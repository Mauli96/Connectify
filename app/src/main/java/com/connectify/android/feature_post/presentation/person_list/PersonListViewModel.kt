package com.connectify.android.feature_post.presentation.person_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.core.data.connectivity.ConnectivityObserver
import com.connectify.android.core.domain.states.NetworkConnectionState
import com.connectify.android.core.domain.use_case.GetOwnUserIdUseCase
import com.connectify.android.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_post.domain.use_case.PostUseCases
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
class PersonListViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val toggleFollowStateForUserUseCase: ToggleFollowStateForUserUseCase,
    private val getOwnUserId: GetOwnUserIdUseCase,
    private val connectivityObserver: ConnectivityObserver,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PersonListState())
    val state = _state.asStateFlow()

    private val _ownUserId = MutableStateFlow("")
    val ownUserId = _ownUserId.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        savedStateHandle.get<String>("parentId")?.let { parentId ->
            getLikesForParent(parentId)
            _ownUserId.value = getOwnUserId()
        }
    }

    fun onEvent(event: PersonListEvent) {
        when(event) {
            is PersonListEvent.ToggleFollowStateForUser -> {
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
                val updatedUsers = state.value.users.map { user ->
                    if(user.userId == userId) {
                        user.copy(
                            isFollowing = !user.isFollowing
                        )
                    } else user
                }

                it.copy(
                    users = updatedUsers
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
                        val updatedUsers = state.value.users.map { user ->
                            if(user.userId == userId) {
                                user.copy(
                                    isFollowing = isFollowing
                                )
                            } else user
                        }

                        it.copy(
                            users = updatedUsers
                        )
                    }
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    private fun getLikesForParent(parentId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = postUseCases.getLikesForParent(parentId)
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