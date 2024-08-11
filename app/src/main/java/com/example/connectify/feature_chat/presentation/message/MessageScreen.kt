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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeMediumSmall
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.feature_chat.domain.model.Message
import com.example.connectify.feature_chat.presentation.message.components.OwnMessage
import com.example.connectify.feature_chat.presentation.message.components.RemoteMessage
import okio.ByteString.Companion.decodeBase64
import java.nio.charset.Charset

@Composable
fun MessageScreen(
    remoteUserId: String,
    remoteUsername: String,
    encodedRemoteUserProfilePictureUrl: String,
    imageLoader: ImageLoader,
    onNavigateUp: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: MessageViewModel = hiltViewModel()
) {

    val decodedRemoteUserProfilePictureUrl = remember {
        encodedRemoteUserProfilePictureUrl.decodeBase64()?.string(Charset.defaultCharset())
    }
    val pagingState = viewModel.pagingState.value
    val state = viewModel.state.value
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = pagingState, key2 = keyboardController) {
        viewModel.messageReceived.collect { event ->
            when(event) {
                is MessageViewModel.MessageUpdateEvent.SingleMessageUpdate,
                is MessageViewModel.MessageUpdateEvent.MessagePageLoaded -> {
                    if(pagingState.items.isEmpty()) {
                        return@collect
                    }
                    lazyListState.scrollToItem(pagingState.items.size - 1)
                }
                is MessageViewModel.MessageUpdateEvent.MessageSent -> {
                    keyboardController?.hide()
                }
            }
        }
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
                        model = decodedRemoteUserProfilePictureUrl,
                        imageLoader = imageLoader
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(ProfilePictureSizeMediumSmall)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(SpaceMedium))
                Text(
                    text = remoteUsername,
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
                items(pagingState.items.size) { i ->
                    val message = pagingState.items[i]
                    if (i >= pagingState.items.size - 1 && !pagingState.endReached && !pagingState.isLoading) {
                        viewModel.loadNextMessages()
                    }
                    if(message.fromId == remoteUserId) {
                        RemoteMessage(
                            message = message.text,
                            formattedTime = message.formattedTime,

                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                    } else {
                        OwnMessage(
                            message = message.text,
                            formattedTime = message.formattedTime,
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }
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