package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_chat.domain.respository.ChatRepository

class DeleteMessage(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(messageId: String): SimpleResource {
        return repository.deleteMessage(messageId)
    }
}