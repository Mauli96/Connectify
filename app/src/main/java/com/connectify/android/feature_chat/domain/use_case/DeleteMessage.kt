package com.connectify.android.feature_chat.domain.use_case

import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_chat.domain.respository.ChatRepository

class DeleteMessage(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(messageId: String): SimpleResource {
        return repository.deleteMessage(messageId)
    }
}