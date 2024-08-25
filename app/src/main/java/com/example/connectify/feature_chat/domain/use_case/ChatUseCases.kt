package com.example.connectify.feature_chat.domain.use_case

data class ChatUseCases(
    val sendMessage: SendMessage,
    val observeChatEvents: ObserveChatEvents,
    val observeMessages: ObserveMessages,
    val getChatsForUser: GetChatsForUser,
    val getMessagesForChat: GetMessagesForChat,
    val initializeRepository: InitializeRepository,
    val deleteChat: DeleteChat,
    val deleteMessage: DeleteMessage
)