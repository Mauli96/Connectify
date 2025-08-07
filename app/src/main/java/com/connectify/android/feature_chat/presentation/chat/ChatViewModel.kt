package com.connectify.android.feature_chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.R
import com.connectify.android.core.data.connectivity.ConnectivityObserver
import com.connectify.android.core.domain.states.NetworkConnectionState
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_chat.domain.use_case.ChatUseCases
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
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state
        .onStart { loadChats() }
        .stateIn(viewModelScope, SharingStarted.Lazily, ChatState())

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: ChatEvent) {
        when(event) {
            is ChatEvent.SelectChat -> {
                _state.update {
                    it.copy(
                        selectedChat = event.chatId,
                        selectedChatName = event.chatName
                    )
                }
            }
            is ChatEvent.DeleteChat -> {
                _state.value.selectedChat?.let { chatId ->
                    deleteChat(chatId)
                }
            }
            is ChatEvent.ShowBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = true
                    )
                }
            }
            is ChatEvent.DismissBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = false
                    )
                }
            }
        }
    }

    fun loadChats() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = chatUseCases.getChatsForUser()
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            chats = result.data ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    _state.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            val result = chatUseCases.deleteChat(chatId)
            when(result) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            chats = state.value.chats.filter { chat ->
                                chat.chatId != chatId
                            },
                            selectedChat = null
                        )
                    }
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_chat
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }
}