package com.connectify.android.feature_chat.domain.use_case

import com.connectify.android.feature_chat.domain.respository.ChatRepository

class CloseWsConnection(
    private val repository: ChatRepository
) {

    suspend operator fun invoke() {
        return repository.close()
    }
}