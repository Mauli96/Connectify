package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.core.util.Resource
import com.example.connectify.feature_chat.domain.model.Chat
import com.example.connectify.feature_chat.domain.respository.ChatRepository

class GetChatsForUser(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(): Resource<List<Chat>> {
        return repository.getChatsForUser()
    }
}