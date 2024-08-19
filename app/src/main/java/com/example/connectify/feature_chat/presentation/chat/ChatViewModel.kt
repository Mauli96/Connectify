package com.example.connectify.feature_chat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_chat.domain.use_case.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
) : ViewModel() {

    private val _state = mutableStateOf(ChatState())
    val state: State<ChatState> = _state

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: ChatEvent) {
        when(event) {
            is ChatEvent.DeleteChatId -> {
                _state.value = state.value.copy(
                    deleteChatId = event.chatId
                )
            }
            is ChatEvent.DeleteChat -> {
                deleteChat(event.chatId)
            }
            is ChatEvent.ShowBottomSheet -> {
                _state.value = state.value.copy(
                    isBottomSheetVisible = true
                )
            }
            is ChatEvent.DismissBottomSheet -> {
                _state.value = state.value.copy(
                    isBottomSheetVisible = false
                )
            }
        }
    }

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            val result = chatUseCases.getChatsForUser()
            when(result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        chats = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    _state.value = state.value.copy(isLoading = false)
                }
            }
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            val result = chatUseCases.deleteChat(chatId)
            when(result) {
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        chats = state.value.chats.filter {
                            it.chatId != chatId
                        }
                    )
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