package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.core.util.Constants.DEFAULT_PAGE_SIZE
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.domain.respository.ChatRepository

class GetMessagesForChat(
    private val repository: ChatRepository
) {

    suspend operator fun invoke(
        chatId: String,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Resource<List<Message>> {
        return repository.getMessagesForChat(
            chatId, page, pageSize
        )
    }
}