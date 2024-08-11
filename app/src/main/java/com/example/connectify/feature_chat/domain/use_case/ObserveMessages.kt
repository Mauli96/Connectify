package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.domain.respository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ObserveMessages(
    private val repository: ChatRepository
) {

    operator fun invoke(): Flow<Message> {
        return repository.observeMessages()
    }
}