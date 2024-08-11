package com.example.connectify.feature_chat.domain.use_case

import com.example.connectify.feature_chat.domain.respository.ChatRepository

class InitializeRepository(
    private val repository: ChatRepository
) {

    operator fun invoke() {
        repository.initialize()
    }
}