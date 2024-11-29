package com.example.connectify.feature_chat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_chat.domain.use_case.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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

    init {
        loadChats()
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
                    _eventFlow.emit(UiEvent.ShowSnackbar(
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
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(UiText.StringResource(
                            R.string.successfully_deleted_chat
                        ))
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }
}