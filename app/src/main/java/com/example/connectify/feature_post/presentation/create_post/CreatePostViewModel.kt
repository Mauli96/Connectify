package com.example.connectify.feature_post.presentation.create_post

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_post.domain.use_case.PostUseCases
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
class CreatePostViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePostState())
    val state = _state.asStateFlow()

    private val _navigationState = MutableStateFlow(false)
    val navigationState = _navigationState.asStateFlow()

    private val _descriptionState = MutableStateFlow(StandardTextFieldState())
    val descriptionState = _descriptionState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> = connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: CreatePostEvent) {
        when(event) {
            is CreatePostEvent.OnEnterDescription -> {
                _descriptionState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is CreatePostEvent.OnPickImage -> {
                _state.update {
                    it.copy(
                        imageUri = event.uri
                    )
                }
            }
            is CreatePostEvent.OnCropImage -> {
                _state.update {
                    it.copy(
                        imageUri = event.uri
                    )
                }
            }
            is CreatePostEvent.OnNavigatingToCrop -> {
                _navigationState.value = true
            }
            is CreatePostEvent.OnNavigatingToBackFromCrop -> {
                _navigationState.value = false
            }
            is CreatePostEvent.OnPostImage -> {
                postImage()
            }
        }
    }

    private fun postImage() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isPosting = true
                )
            }
            val result = postUseCases.createPost(
                description = descriptionState.value.text,
                imageUri = state.value.imageUri
            )
            when(result) {
                is Resource.Success ->{
                    try {
                        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                    } catch(e: SecurityException) {
                        _eventFlow.send(UiEvent.ShowSnackbar(
                            uiText = UiText.StringResource(R.string.notification_permission_denied)
                        ))
                    }

                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = UiText.StringResource(R.string.post_created)
                    ))
                    _eventFlow.send(UiEvent.Navigate(Screen.MainFeedScreen.route))
                    _descriptionState.value = StandardTextFieldState()
                    _state.update {
                        it.copy(
                            imageUri = null,
                            isPosting = false
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    _state.update {
                        it.copy(
                            isPosting = false
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}