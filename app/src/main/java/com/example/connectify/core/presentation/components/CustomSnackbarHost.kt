package com.example.connectify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Screen
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit = {},
    iconProvider: @Composable (String) -> Painter = { message ->
        // Define string resources
        val successRegisterationText = stringResource(R.string.success_registeration)
        val successLoginText = stringResource(R.string.success_login)
        val createPostText = stringResource(R.string.post_created)
        val savePostText = stringResource(R.string.successfully_saved_post)
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
                    message.contains(savePostText, ignoreCase = true) -> painterResource(R.drawable.check_icon)
            message.contains(unSavePostText, ignoreCase = true) ||
                    message.contains(deletePostText, ignoreCase = true) ||
                    message.contains(deleteChatText, ignoreCase = true) ||
                    message.contains(deleteCommentText, ignoreCase = true) -> painterResource(R.drawable.remove_icon)
            message.contains(errorUnknownText, ignoreCase = true) ||
                    message.contains(errorServerText, ignoreCase = true) ||
                    message.contains(errorWentWrongText, ignoreCase = true) -> painterResource(R.drawable.error_icon)
            else -> painterResource(R.drawable.info_icon)
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
            val icon = iconProvider(snackbarData.message)
            val showAction = snackbarData.message.contains(stringResource(R.string.successfully_saved_post), ignoreCase = true)

            Snackbar(
                modifier = Modifier.padding(SpaceMedium),
                shape = MaterialTheme.shapes.medium,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = icon,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                        Text(
                            text = snackbarData.message,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(start = SpaceSmall)
                                .weight(1f)
                        )
                    }
                },
                action = {
                    if(showAction) {
                        snackbarData.actionLabel?.let { actionLabel ->
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