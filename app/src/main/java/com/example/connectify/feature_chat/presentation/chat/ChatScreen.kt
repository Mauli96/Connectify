package com.example.connectify.feature_chat.presentation.chat

import android.util.Base64
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_chat.domain.model.Chat

@Composable
fun ChatScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chats = viewModel.state.value.chats
    val isLoading = viewModel.state.value.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            StandardToolbar(
                title = {
                    Text(
                        text = stringResource(id = R.string.message_screen),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(chats) { chat ->
                    ChatItem(
                        item = chat,
                        imageLoader = imageLoader,
                        modifier = Modifier.fillMaxWidth(),
                        onItemClick = {
                            onNavigate(Screen.MessageScreen.route + "/${chat.remoteUserId}/${chat.remoteUsername}/${Base64.encodeToString(chat.remoteUserProfilePictureUrl.encodeToByteArray(), 0)}?chatId=${chat.chatId}")
                        }
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
        if(isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Center),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                trackColor = Color.White
            )
        }
    }
}