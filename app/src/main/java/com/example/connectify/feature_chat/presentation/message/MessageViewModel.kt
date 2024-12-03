package com.example.connectify.feature_chat.presentation.message

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.domain.use_case.GetOwnProfilePictureUseCase
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.domain.use_case.ChatUseCases
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(MessageState())
    val state = _state.asStateFlow()

    private val _messageTextFieldState = MutableStateFlow(StandardTextFieldState())
    val messageTextFieldState = _messageTextFieldState.asStateFlow()

    private val _profilePictureState = MutableStateFlow("")
    val profilePictureState = _profilePictureState
        .onStart { getOwnProfilePicture() }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _pagingState = MutableStateFlow<PagingState<Message>>(PagingState())
    val pagingState = _pagingState
        .onStart {
            loadInitialMessages()
            observeChatEvents()
            observeChatMessages()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    private val _messageUpdatedEvent = MutableSharedFlow<MessageUpdateEvent>(replay = 1)
    val messageReceived = _messageUpdatedEvent.asSharedFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        chatUseCases.initializeRepository()
    }

    private val paginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingState.update {
                it.copy(
                    isLoading = isLoading
                )
            }
        },
        onRequest = { nextPage ->
            savedStateHandle.get<String>("chatId")?.let { chatId ->
                chatUseCases.getMessagesForChat(
                    chatId, nextPage
                )
            } ?: Resource.Error(UiText.unknownError())
        },
        onError = { errorUiText ->
            _eventFlow.emit(UiEvent.ShowSnackbar(errorUiText))
        },
        onSuccess = { messages, firstPage ->
            _pagingState.update {
                it.copy(
                    items = if(firstPage) messages else pagingState.value.items + messages,
                    endReached = messages.isEmpty()
                )
            }
            viewModelScope.launch {
                _messageUpdatedEvent.emit(MessageUpdateEvent.MessagePageLoaded)
            }
        }
    )

    fun onEvent(event: MessageEvent) {
        when(event) {
            is MessageEvent.EnteredMessage -> {
                _messageTextFieldState.update {
                    it.copy(text = event.message)
                }
                _state.update {
                    it.copy(
                        canSendMessage = event.message.isNotBlank()
                    )
                }
            }
            is MessageEvent.SendMessage -> {
                sendMessage()
            }
            is MessageEvent.SelectMessage -> {
                _state.update {
                    it.copy(
                        selectedMessageId = event.messageId
                    )
                }
            }
            is MessageEvent.DeleteMessage -> {
                _state.value.selectedMessageId?.let { messageId ->
                    deleteMessage(messageId)
                }
            }
            is MessageEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isDialogVisible = true
                    )
                }
            }
            is MessageEvent.DismissDialog -> {
                _state.update {
                    it.copy(
                        isDialogVisible = false
                    )
                }
            }
        }
    }

    private fun observeChatMessages() {
        viewModelScope.launch {
            chatUseCases.observeMessages()
                .collect { message ->
                    _pagingState.update {
                        it.copy(
                            items = pagingState.value.items + message
                        )
                    }
                    _messageUpdatedEvent.emit(MessageUpdateEvent.SingleMessageUpdate)
                }
        }
    }

    private fun observeChatEvents() {
        chatUseCases.observeChatEvents()
            .onEach { event ->
                when(event) {
                    is WebSocket.Event.OnConnectionOpened<*> -> {
                        println("Connection was opened")
                    }
                    is WebSocket.Event.OnConnectionFailed -> {
                        println("Connection failed: ${event.throwable}")
                    }
                    else -> {
                        null
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun loadInitialMessages() {
        viewModelScope.launch {
            paginator.loadFirstItems()
        }
    }

    fun loadNextMessages() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun sendMessage() {
        val toId = savedStateHandle.get<String>("remoteUserId") ?: return
        if(messageTextFieldState.value.text.isBlank()) {
            return
        }
        val chatId = savedStateHandle.get<String>("chatId")
        chatUseCases.sendMessage(
            toId = toId,
            text = messageTextFieldState.value.text,
            chatId = chatId
        )
        _messageTextFieldState.value = StandardTextFieldState()
        _state.update {
            it.copy(
                canSendMessage = false
            )
        }
    }

    private fun getOwnProfilePicture() {
        viewModelScope.launch {
            val result = getOwnProfilePictureUseCase()
            when(result) {
                is Resource.Success -> {
                    _profilePictureState.value = result.data.toString()
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

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val result = chatUseCases.deleteMessage(messageId)
            when(result) {
                is Resource.Success -> {
                    _pagingState.update {
                        it.copy(
                            items = pagingState.value.items.filter {
                                it.id != messageId
                            }
                        )
                    }
                    _state.update {
                        it.copy(
                            selectedMessageId = null
                        )
                    }
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

    sealed class MessageUpdateEvent {
        object SingleMessageUpdate: MessageUpdateEvent()
        object MessagePageLoaded: MessageUpdateEvent()
    }
}