package com.example.connectify.feature_profile.presentation.follower

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.domain.use_case.GetOwnUserIdUseCase
import com.example.connectify.core.domain.use_case.ToggleFollowStateForUserUseCase
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val toggleFollowStateForUserUseCase: ToggleFollowStateForUserUseCase,
    private val getOwnUserId: GetOwnUserIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(FollowerState())
    val state: State<FollowerState> = _state

    private val _ownUserId = mutableStateOf("")
    val ownUserId: State<String> = _ownUserId

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            getFollowedToUser(userId)
            _ownUserId.value = getOwnUserId()
        }
    }

    fun onEvent(event: FollowerEvent) {
        when(event) {
            is FollowerEvent.ToggleFollowStateForUser -> {
                toggleFollowStateForUser(event.userId)
            }
        }
    }

    private fun toggleFollowStateForUser(userId: String) {
        viewModelScope.launch {
            val isFollowing = state.value.users.find {
                it.userId == userId
            }?.isFollowing == true

            _state.value = state.value.copy(
                users = state.value.users.map {
                    if(it.userId == userId) {
                        it.copy(
                            isFollowing = !it.isFollowing
                        )
                    } else it
                }
            )

            val result = toggleFollowStateForUserUseCase(
                userId = userId,
                isFollowing = isFollowing
            )
            when(result) {
                is Resource.Success -> Unit
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        users = state.value.users.map {
                            if(it.userId == userId) {
                                it.copy(isFollowing = isFollowing)
                            } else it
                        }
                    )
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }

    private fun getFollowedToUser(userId: String) {
        viewModelScope.launch {
            _state.value = state.value.copy(
                isLoading = true
            )
            val result = profileUseCases.getFollowedToUser(userId)
            when(result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        users = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false
                    )
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(result.uiText ?: UiText.unknownError())
                    )
                }
            }
        }
    }
}