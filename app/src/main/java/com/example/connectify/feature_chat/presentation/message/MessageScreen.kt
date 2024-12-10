package com.example.connectify.feature_chat.presentation.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.SendTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.GreenAccent
import com.example.connectify.core.presentation.ui.theme.LottieIconSize
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeMediumSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceLargeExtra
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.feature_activity.presentation.util.DateFormatUtil
import com.example.connectify.feature_chat.presentation.message.components.OwnMessage
import com.example.connectify.feature_chat.presentation.message.components.RemoteMessage
import kotlinx.coroutines.flow.collectLatest
import okio.ByteString.Companion.decodeBase64
import java.nio.charset.Charset

@Composable
fun MessageScreen(
    remoteUserId: String,
    remoteUsername: String,
    isOnline: Boolean,
    lastSeen: Long,
    snackbarHostState: SnackbarHostState,
    encodedRemoteUserProfilePictureUrl: String,
    imageLoader: ImageLoader,
    onNavigateUp: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: MessageViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val messageTextFieldState by viewModel.messageTextFieldState.collectAsStateWithLifecycle()
    val profilePictureState by viewModel.profilePictureState.collectAsStateWithLifecycle()
    val pagingState by viewModel.pagingState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    val decodedRemoteUserProfilePictureUrl = remember {
        encodedRemoteUserProfilePictureUrl.decodeBase64()?.string(Charset.defaultCharset())
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.hii_message))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context)
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    Column {
                        Text(
                            text = remoteUsername,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if(isOnline) {
                            Text(
                                text = stringResource(id = R.string.online),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GreenAccent
                                )
                            )
                        } else {
                            Text(
                                text = DateFormatUtil.timestampToFormattedString(lastSeen, "hh:mm a, dd MMM"),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                    }
                }
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if(!pagingState.isLoading && pagingState.items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = SpaceLargeExtra),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimation(
                            modifier = Modifier.size(LottieIconSize),
                            composition = composition,
                            progress = {
                                progress
                            },
                        )
                        Text(
                            text = stringResource(R.string.no_messages_found),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = SpaceMedium,
                                end = SpaceMedium
                            )
                            .imePadding()
                    ) {
                        items(
                            count = pagingState.items.size,
                            key = { i ->
                                val message = pagingState.items[i]
                                message.id
                            }
                        ) { i ->
                            val message = pagingState.items[i]
                            if(i >= pagingState.items.size - 1 && !pagingState.endReached && !pagingState.isLoading) {
                                viewModel.loadNextMessages()
                            }
                            if(message.fromId == remoteUserId) {
                                RemoteMessage(
                                    message = message,
                                    formattedTime = message.formattedTime
                                )
                            } else {
                                OwnMessage(
                                    message = message,
                                    formattedTime = message.formattedTime,
                                    onLongPress = { id ->
                                        viewModel.onEvent(MessageEvent.SelectMessage(id))
                                        viewModel.onEvent(MessageEvent.ShowDialog)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        }
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
                                text = stringResource(id = R.string.delete_message),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.this_action_cannot_be_undone),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(SpaceMedium))
                            Row {
                                Button(onClick = {
                                    viewModel.onEvent(MessageEvent.DismissDialog)
                                }) {
                                    Text(text = stringResource(id = R.string.cancel))
                                }
                                Spacer(modifier = Modifier.width(SpaceLarge))
                                Button(
                                    onClick = {
                                        viewModel.onEvent(MessageEvent.DeleteMessage)
                                        viewModel.onEvent(MessageEvent.DismissDialog)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text(text = stringResource(id = R.string.delete))
                                }
                            }
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
            SendTextField(
                state = messageTextFieldState,
                canSendMessage = state.canSendMessage,
                onValueChange = {
                    viewModel.onEvent(MessageEvent.EnteredMessage(it))
                },
                onSend = {
                    viewModel.onEvent(MessageEvent.SendMessage)
                },
                ownProfilePicture = profilePictureState,
                hint = stringResource(id = R.string.enter_a_message)
            )
        }
        if(pagingState.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}