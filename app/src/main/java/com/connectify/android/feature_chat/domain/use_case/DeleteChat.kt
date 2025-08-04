package com.connectify.android.feature_chat.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_chat.domain.respository.ChatRepository

class DeleteChat(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(chatId: String): SimpleResource {
        return repository.deleteChat(chatId)
    }
}