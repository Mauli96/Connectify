package com.example.connectify.feature_chat.presentation.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.components.SendTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeMediumSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
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

    LaunchedEffect(key1 = pagingState) {
        viewModel.messageReceived.collect { event ->
            when(event) {
                is MessageViewModel.MessageUpdateEvent.SingleMessageUpdate,
                is MessageViewModel.MessageUpdateEvent.MessagePageLoaded -> {
                    if(pagingState.items.isEmpty()) {
                        return@collect
                    }
                    lazyListState.scrollToItem(pagingState.items.size - 1)
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
                state = lazyListState,
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
                            formattedTime = message.formattedTime
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    } else {
                        OwnMessage(
                            message = message.text,
                            formattedTime = message.formattedTime,
                            onLongPress = {
                                viewModel.onEvent(MessageEvent.ShowDialog)
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
            if(state.isDialogVisible) {
                Dialog(
                    onDismissRequest = {
                        viewModel.onEvent(MessageEvent.DismissDialog)
                    }
                ) {
                    Column {
                        Text(
                            text = "Delete Message?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Text(
                            text = "This action cannot be undone.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Row {
                            Button(onClick = {
                                viewModel.onEvent(MessageEvent.DismissDialog)
                            }) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(SpaceLarge))
                            Button(
                                onClick = {

                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text(text = "Delete")
                            }
                        }
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
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
                hint = stringResource(id = R.string.enter_a_message)
            )
        }
    }
}