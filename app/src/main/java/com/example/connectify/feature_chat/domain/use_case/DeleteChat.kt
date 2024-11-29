package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_chat.domain.respository.ChatRepository

class DeleteChat(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(chatId: String): SimpleResource {
        return repository.deleteChat(chatId)
    }
}