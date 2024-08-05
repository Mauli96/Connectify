package com.example.connectify.feature_chat.presentation.message

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor() : ViewModel() {

    private val _messageTextFieldState = mutableStateOf(StandardTextFieldState())
    val messageTextFieldState: State<StandardTextFieldState> = _messageTextFieldState

    private val _state = mutableStateOf(MessageState())
    val state: State<MessageState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: MessageEvent) {
        when(event) {
            is MessageEvent.EnteredMessage -> {
                _messageTextFieldState.value = messageTextFieldState.value.copy(
                    text = event.message
                )
                _state.value = state.value.copy(
                    canSendMessage = event.message.isNotBlank()
                )
            }
            is MessageEvent.SendMessage -> {

            }
        }
    }
}