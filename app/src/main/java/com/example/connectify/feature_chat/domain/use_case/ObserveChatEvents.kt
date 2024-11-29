package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.feature_chat.domain.respository.ChatRepository
import com.tinder.scarlet.WebSocket
import kotlinx.coroutines.flow.Flow

class ObserveChatEvents(
    private val repository: ChatRepository
) {

    operator fun invoke(): Flow<WebSocket.Event> {
        return repository.observeChatEvents()
    }
}