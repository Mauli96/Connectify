package com.connectify.android.feature_chat.domain.use_case

import com.connectify.android.core.util.Resource
import com.connectify.android.feature_chat.domain.model.Chat
import com.connectify.android.feature_chat.domain.respository.ChatRepository

class GetChatsForUser(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(): Resource<List<Chat>> {
        return repository.getChatsForUser()
    }
}