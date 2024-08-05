package com.example.connectify.feature_chat.presentation.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.components.SendTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeExtraSmall
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.presentation.message.components.OwnMessage
import com.example.connectify.feature_chat.presentation.message.components.RemoteMessage

@Composable
fun MessageScreen(
    imageLoader: ImageLoader,
    onNavigateUp: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: MessageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val messages = remember {
        listOf(
            Message(
                fromId = "",
                toId = "",
                text = "Hii",
                formattedTime = "10:30",
                chatId = ""
            ),
            Message(
                fromId = "",
                toId = "",
                text = "How Are you?",
                formattedTime = "10:30",
                chatId = ""
            ),
            Message(
                fromId = "",
                toId = "",
                text = "Contrary to popular belief, Lorem Ipsum is not simply random text.",
                formattedTime = "10:30",
                chatId = ""
            ),
            Message(
                fromId = "",
                toId = "",
                text = ".",
                formattedTime = "10:30",
                chatId = ""
            ),
            Message(
                fromId = "",
                toId = "",
                text = "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable.",
                formattedTime = "10:30",
                chatId = ""
            ),
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            onNavigateUp = onNavigateUp,
            showBackArrow = true,
            title = {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "http://192.168.0.209:8001/profile_pictures/055b1919-cffc-4296-b76e-4dd86452de05.jpg",
                        imageLoader = imageLoader
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(ProfilePictureSizeExtraSmall)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(SpaceMedium))
                Text(
                    text = "mauli.waghmore",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(SpaceMedium)
            ) {
                items(messages) { message ->
                    RemoteMessage(
                        message = message.text,
                        formattedTime = message.formattedTime
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                    OwnMessage(
                        message = message.text,
                        formattedTime = message.formattedTime
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
            SendTextField(
                state = viewModel.messageTextFieldState.value,
                canSendMessage = state.canSendMessage,
                onValueChange = {
                    viewModel.onEvent(MessageEvent.EnteredMessage(it))
                },
                onSend = {
                    viewModel.onEvent(MessageEvent.SendMessage)
                },
                hint = stringResource(id = R.string.enter_a_message),
                backgroundColor = MaterialTheme.colorScheme.background
            )
        }
    }
}