package com.example.connectify.feature_chat.presentation.message

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.domain.use_case.GetOwnProfilePictureUseCase
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.domain.use_case.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val getOwnProfilePictureUseCase: GetOwnProfilePictureUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val connectivityObserver: ConnectivityObserver
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
            observeChatMessages()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    private val _messageUpdatedEvent = MutableSharedFlow<MessageUpdateEvent>(replay = 1)
    val messageReceived = _messageUpdatedEvent.asSharedFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    private val viewModelJob = SupervisorJob()
    private val viewModelCoroutine = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val paginator = DefaultPaginator(
        onFirstLoadUpdated = { isFirstLoading ->
            _pagingState.update {
                it.copy(
                    isFirstLoading = isFirstLoading
                )
            }
        },
        onNextLoadUpdated = { isNextLoading ->
            _pagingState.update {
                it.copy(
                    isNextLoading = isNextLoading
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
            _eventFlow.send(UiEvent.ShowSnackbar(errorUiText))
        },
        onSuccess = { messages, firstPage ->
            _pagingState.update {
                it.copy(
                    items = if(firstPage) {
                        messages
                    } else {
                        pagingState.value.items + messages
                    },
                    endReached = messages.isEmpty()
                )
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
                        selectedMessageId = event.messageId,
                        selectedMessage = event.message
                    )
                }
            }
            is MessageEvent.DeleteMessage -> {
                _state.value.selectedMessageId?.let { messageId ->
                    deleteMessage(messageId)
                }
            }
            is MessageEvent.ShowBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = true
                    )
                }
            }
            is MessageEvent.DismissBottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetVisible = false
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
                            items = if(pagingState.value.items.size > 14) {
                                listOf(message) + pagingState.value.items.dropLast(1)
                            } else {
                                listOf(message) + pagingState.value.items
                            }
                        )
                    }
                    _messageUpdatedEvent.emit(MessageUpdateEvent.SingleMessageUpdate)
                }
        }
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
        viewModelScope.launch {
            val toId = savedStateHandle.get<String>("remoteUserId") ?: return@launch
            if(messageTextFieldState.value.text.isBlank()) {
                return@launch
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
    }

    private fun getOwnProfilePicture() {
        viewModelScope.launch {
            val result = getOwnProfilePictureUseCase()
            when(result) {
                is Resource.Success -> {
                    _profilePictureState.value = result.data.toString()
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
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = result.uiText ?: UiText.unknownError()
                        )
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelCoroutine.launch {
            try {
                chatUseCases.closeWsConnection()
            } finally {
                viewModelJob.cancel()
            }
        }
    }

    sealed class MessageUpdateEvent {
        object SingleMessageUpdate: MessageUpdateEvent()
    }
}