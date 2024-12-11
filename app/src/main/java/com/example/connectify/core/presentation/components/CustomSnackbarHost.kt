package com.example.connectify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Screen
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit = {},
    lottieIconProvider: @Composable (String) -> Int = { message ->
        // Define string resources
        val successRegisterationText = stringResource(R.string.success_registeration)
        val successLoginText = stringResource(R.string.success_login)
        val createPostText = stringResource(R.string.post_created)
        val savePostText = stringResource(R.string.successfully_saved_post)
        val successDownload = stringResource(R.string.successfully_downloaded_post)
        val unSavePostText = stringResource(R.string.successfully_unsaved_post)
        val deletePostText = stringResource(R.string.successfully_deleted_post)
        val deleteChatText = stringResource(R.string.successfully_deleted_chat)
        val deleteCommentText = stringResource(R.string.successfully_deleted_chat)
        val errorUnknownText = stringResource(R.string.error_unknown)
        val errorServerText = stringResource(R.string.error_couldnt_reach_server)
        val errorWentWrongText = stringResource(R.string.oops_something_went_wrong)


        // Return the appropriate icon based on the message
        when {
            message.contains(successRegisterationText, ignoreCase = true) ||
                    message.contains(successLoginText, ignoreCase = true) ||
                    message.contains(createPostText, ignoreCase = true) ||
                    message.contains(savePostText, ignoreCase = true) ||
                    message.contains(successDownload, ignoreCase = true) -> R.raw.success
            message.contains(unSavePostText, ignoreCase = true) ||
                    message.contains(deletePostText, ignoreCase = true) ||
                    message.contains(deleteChatText, ignoreCase = true) ||
                    message.contains(deleteCommentText, ignoreCase = true) -> R.raw.delete
            message.contains(errorUnknownText, ignoreCase = true) ||
                    message.contains(errorServerText, ignoreCase = true) ||
                    message.contains(errorWentWrongText, ignoreCase = true) -> R.raw.error
            else -> R.raw.info
        }
    }
) {

    val currentSnackbarData by rememberUpdatedState(
        newValue = snackbarHostState.currentSnackbarData
    )

    var isVisible by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(currentSnackbarData) {
        if(currentSnackbarData != null) {
            isVisible = true
            delay(3000)
            isVisible = false
            delay(3000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        currentSnackbarData?.let { snackbarData ->
            val animationResource = lottieIconProvider(snackbarData.visuals.message)
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResource))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1
            )
            val showAction = snackbarData.visuals.message.contains(stringResource(R.string.successfully_saved_post), ignoreCase = true)

            Snackbar(
                modifier = Modifier.padding(SpaceMedium),
                shape = MaterialTheme.shapes.medium,
                containerColor = Color(0xff322f2f),
                contentColor = MaterialTheme.colorScheme.onBackground,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LottieAnimation(
                            modifier = Modifier.size(IconSizeMedium),
                            composition = composition,
                            progress = {
                                progress
                            }
                        )
                        Text(
                            text = snackbarData.visuals.message,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = SpaceSmall)
                                .weight(1f)
                        )
                    }
                },
                action = {
                    if(showAction) {
                        snackbarData.visuals.actionLabel?.let { actionLabel ->
                            TextButton(
                                onClick = {
                                    snackbarData.performAction()
                                    onNavigate(Screen.SavedPostScreen.route)
                                }
                            ) {
                                Text(
                                    text = actionLabel.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}