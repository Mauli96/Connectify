package com.example.connectify.feature_chat.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.ImageLoader
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_chat.domain.model.Chat

@Composable
fun ChatScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
) {

    val chats = remember {
        listOf(
            Chat(
                chatId = "1234",
                remoteUserId = "66a31fd32e5b1c28c13bc475",
                remoteUsername = "mauli.waghmore",
                remoteUserProfilePictureUrl = "http://192.168.0.209:8001/profile_pictures/055b1919-cffc-4296-b76e-4dd86452de05.jpg",
                lastMessage = "A paragram is a play on words or pun that involves changing the letters of a word, especially the first letter.",
                timestamp = 1921220
            ),
            Chat(
                chatId = "1234",
                remoteUserId = "66a31fd32e5b1c28c13bc475",
                remoteUsername = "mauli.waghmore",
                remoteUserProfilePictureUrl = "http://192.168.0.209:8001/profile_pictures/055b1919-cffc-4296-b76e-4dd86452de05.jpg",
                lastMessage = "A paragram is a play on words or pun that involves changing the letters of a word, especially the first letter.",
                timestamp = 1921220
            ),
            Chat(
                chatId = "1234",
                remoteUserId = "66a31fd32e5b1c28c13bc475",
                remoteUsername = "mauli.waghmore",
                remoteUserProfilePictureUrl = "http://192.168.0.209:8001/profile_pictures/055b1919-cffc-4296-b76e-4dd86452de05.jpg",
                lastMessage = "A paragram is a play on words or pun that involves changing the letters of a word, especially the first letter.",
                timestamp = 1921220
            ),
            Chat(
                chatId = "1234",
                remoteUserId = "66a31fd32e5b1c28c13bc475",
                remoteUsername = "mauli.waghmore",
                remoteUserProfilePictureUrl = "http://192.168.0.209:8001/profile_pictures/055b1919-cffc-4296-b76e-4dd86452de05.jpg",
                lastMessage = "A paragram is a play on words or pun that involves changing the letters of a word, especially the first letter.",
                timestamp = 1921220
            ),
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(chats) { chat ->
                ChatItem(
                    item = chat,
                    imageLoader = imageLoader,
                    onItemClick = {
                        onNavigate(Screen.MessageScreen.route)
                    }
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}