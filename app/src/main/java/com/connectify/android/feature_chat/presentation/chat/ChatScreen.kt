package com.connectify.android.feature_chat.presentation.chat

import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.StandardBottomSheet
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.ui.theme.LottieIconSize
import com.connectify.android.core.presentation.ui.theme.SpaceLargeExtra
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.util.Screen
import com.connectify.android.feature_chat.presentation.chat.components.ChatItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_chats_found))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when(event) {
            is UiEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.uiText.asString(context)
                )
            }
            else -> {}
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
                title = {
                    Text(
                        text = stringResource(id = R.string.message_screen),
                        style = Typography.titleLarge
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if(state.chats.isEmpty() && !state.isLoading) {
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
                            progress = { progress }
                        )
                        Text(
                            text = stringResource(R.string.no_chats_found),
                            style = Typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(
                            items = state.chats,
                            key = { chat ->
                                chat.chatId
                            }
                        ) { chat ->
                            ChatItem(
                                item = chat,
                                imageLoader = imageLoader,
                                context = context,
                                modifier = Modifier.fillMaxWidth(),
                                onItemClick = {
                                    onNavigate(Screen.MessageScreen.route + "/${chat.remoteUserId}/${chat.remoteUsername}/${Base64.encodeToString(chat.remoteUserProfilePictureUrl.encodeToByteArray(), 0)}?chatId=${chat.chatId}?isOnline=${chat.online}?lastSeen=${chat.lastSeen}")
                                },
                                onLongPress = {
                                    viewModel.onEvent(ChatEvent.SelectChat(chat.chatId, chat.remoteUsername))
                                    viewModel.onEvent(ChatEvent.ShowBottomSheet)
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
                ConnectivityBanner(
                    networkState = networkState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }
        if(state.isBottomSheetVisible) {
            StandardBottomSheet(
                title = state.selectedChatName.toString(),
                bottomSheetState = bottomSheetState,
                showDeleteOption = true,
                onDismissRequest = {
                    viewModel.onEvent(ChatEvent.DismissBottomSheet)
                },
                onDeleteClick = {
                    viewModel.onEvent(ChatEvent.DeleteChat)
                    viewModel.onEvent(ChatEvent.DismissBottomSheet)
                },
                onCancelClick = {
                    viewModel.onEvent(ChatEvent.DismissBottomSheet)
                }
            )
        }
        if(state.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}