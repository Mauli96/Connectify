package com.connectify.android.feature_chat.presentation.message

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.SendTextField
import com.connectify.android.core.presentation.components.StandardMessageSheet
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.ui.theme.GreenAccent
import com.connectify.android.core.presentation.ui.theme.LottieIconSize
import com.connectify.android.core.presentation.ui.theme.ProfilePictureSizeMediumSmall
import com.connectify.android.core.presentation.ui.theme.SpaceLargeExtra
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.util.Constants
import com.connectify.android.feature_activity.presentation.util.DateFormatUtil
import com.connectify.android.feature_chat.presentation.message.components.OwnMessage
import com.connectify.android.feature_chat.presentation.message.components.RemoteMessage
import okio.ByteString.Companion.decodeBase64
import java.nio.charset.Charset

@OptIn(ExperimentalMaterial3Api::class)
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
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()
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

    LaunchedEffect(key1 = pagingState) {
        viewModel.messageReceived.collect { event ->
            when(event) {
                is MessageViewModel.MessageUpdateEvent.SingleMessageUpdate -> {
                    if(pagingState.items.isEmpty()) {
                        return@collect
                    }
                    lazyListState.scrollToItem(0)
                }
            }
        }
    }

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
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                showBackArrow = true,
                title = {
                    Row {
                        AsyncImage(
                            model = decodedRemoteUserProfilePictureUrl,
                            contentDescription = stringResource(R.string.profile_image),
                            imageLoader = imageLoader,
                            modifier = Modifier
                                .size(ProfilePictureSizeMediumSmall)
                                .clip(CircleShape),
                        )
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        Column {
                            Text(
                                text = remoteUsername,
                                style = Typography.labelLarge.withSize(17.sp)
                            )
                            if(isOnline) {
                                Text(
                                    text = stringResource(id = R.string.online),
                                    style = Typography.labelSmall
                                        .withSize(12.sp)
                                        .withColor(GreenAccent)
                                )
                            } else {
                                Text(
                                    text = DateFormatUtil.timestampToFormattedString(lastSeen, "hh:mm a, dd MMM"),
                                    style = Typography.labelSmall.withSize(12.sp)
                                )
                            }

                        }
                    }
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                if(pagingState.items.isEmpty() && !pagingState.isFirstLoading && !pagingState.isNextLoading ) {
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
                            style = Typography.labelSmall,
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
                            ),
                        reverseLayout = true
                    ) {
                        items(
                            count = pagingState.items.size,
                            key = { i ->
                                val message = pagingState.items[i]
                                message.id
                            }
                        ) { i ->
                            val message = pagingState.items[i]
                            if(i >= pagingState.items.size - 1 && pagingState.items.size >= Constants.DEFAULT_PAGE_SIZE
                                && !pagingState.endReached && !pagingState.isFirstLoading && !pagingState.isNextLoading) {
                                viewModel.loadNextMessages()
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            if(message.fromId == remoteUserId) {
                                RemoteMessage(
                                    message = message,
                                    formattedTime = message.formattedTime,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                OwnMessage(
                                    message = message,
                                    formattedTime = message.formattedTime,
                                    onLongPress = {
                                        viewModel.onEvent(MessageEvent.SelectMessage(message.id, message.text))
                                        viewModel.onEvent(MessageEvent.ShowBottomSheet)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        if(pagingState.isNextLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = SpaceMedium)
                                        .background(Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CustomCircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
            SendTextField(
                state = messageTextFieldState,
                imageLoader = imageLoader,
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
        ConnectivityBanner(
            networkState = networkState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 55.dp)
        )
        if(state.isBottomSheetVisible) {
            StandardMessageSheet(
                bottomSheetState = bottomSheetState,
                showDeleteOption = true,
                showCopyOption = true,
                onDismissRequest = {
                    viewModel.onEvent(MessageEvent.DismissBottomSheet)
                },
                onCopiedClick = {
                    copyToClipboard(
                        context = context,
                        text = state.selectedMessage.toString()
                    )
                    viewModel.onEvent(MessageEvent.DismissBottomSheet)
                },
                onDeleteClick = {
                    viewModel.onEvent(MessageEvent.DeleteMessage)
                    viewModel.onEvent(MessageEvent.DismissBottomSheet)
                },
                onCancelClick = {
                    viewModel.onEvent(MessageEvent.DismissBottomSheet)
                }
            )
        }
        if(pagingState.isFirstLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}

fun copyToClipboard(
    context: Context,
    text: String
) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Message", text)
    clipboardManager.setPrimaryClip(clip)
    Toast.makeText(context, "Message copied!", Toast.LENGTH_SHORT).show()
}