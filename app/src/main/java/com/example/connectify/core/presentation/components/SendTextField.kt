package com.example.connectify.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.connectify.R
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall

@Composable
fun SendTextField(
    state: StandardTextFieldState,
    onValueChange: (String) -> Unit,
    maxLength: Int = 1000,
    maxLines: Int = 5,
    onSend: () -> Unit,
    hint: String = "",
    canSendMessage: Boolean = true,
    isLoading: Boolean = false,
    focusRequester: FocusRequester = FocusRequester()
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .padding(
                start = SpaceMedium,
                end = SpaceMedium,
                bottom = SpaceSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(48.dp)
                .shadow(16.dp)
                .focusRequester(focusRequester = focusRequester),
            value = state.text,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                textColor = MaterialTheme.colorScheme.onPrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if(it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            shape = RoundedCornerShape(20.dp),
            maxLines = maxLines,
            singleLine = false,
            placeholder = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            trailingIcon = {
                if(state.error == null && canSendMessage) {
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    if(isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        IconButton(
                            onClick = onSend,
                            modifier = Modifier
                                .size(30.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.send_icon),
                                contentDescription = stringResource(id = R.string.send_comment),
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}