package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.feature_chat.domain.respository.ChatRepository

class SendMessage(
    private val repository: ChatRepository
) {

    operator fun invoke(toId: String, text: String, chatId: String?) {
        if(text.isBlank()) {
            return
        }
        repository.sendMessage(toId, text.trim(), chatId)
    }
}