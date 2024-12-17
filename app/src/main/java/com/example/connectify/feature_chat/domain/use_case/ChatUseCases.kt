package com.example.connectify.feature_chat.domain.use_case

data class ChatUseCases(
    val sendMessage: SendMessage,
    val observeMessages: ObserveMessages,
    val getChatsForUser: GetChatsForUser,
    val getMessagesForChat: GetMessagesForChat,
    val closeWsConnection: CloseWsConnection,
    val deleteChat: DeleteChat,
    val deleteMessage: DeleteMessage
)