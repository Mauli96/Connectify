package com.example.connectify.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeExtraSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall

@Composable
fun SendTextField(
    modifier: Modifier = Modifier,
    state: StandardTextFieldState,
    onValueChange: (String) -> Unit,
    ownProfilePicture: String = "",
    maxLength: Int = 1000,
    maxLines: Int = 5,
    onSend: () -> Unit,
    hint: String = "",
    canSendMessage: Boolean = true,
    isUploading: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        BasicTextField(
            value = state.text,
            onValueChange = {
                if(it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 45.dp)
                .background(MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(0.dp))
                .focusRequester(focusRequester = focusRequester),
            maxLines = maxLines,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if(canSendMessage && !isUploading && state.error == null) {
                        onSend()
                    }
                }
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ownProfilePicture
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(ProfilePictureSizeExtraSmall)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(Modifier.weight(1f)) {
                        if(state.text.isEmpty()) {
                            Text(
                                text = hint,
                                style = MaterialTheme.typography.displaySmall
                            )
                        }
                        innerTextField()
                    }
                    if(state.error == null && canSendMessage) {
                        Spacer(modifier = Modifier.width(SpaceMedium))
                        if(isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(IconSizeMedium)
                                    .padding(end = 3.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(
                                onClick = onSend,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_send),
                                    contentDescription = stringResource(id = R.string.send),
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(SpaceSmall))
                    }
                }
            }
        )
    }
}