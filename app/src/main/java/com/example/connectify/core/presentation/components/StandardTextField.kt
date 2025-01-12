package com.example.connectify.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.ui.theme.withSize
import com.example.connectify.core.util.TestTags

@Composable
fun StandardTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    hint: String = "",
    maxLength: Int = 400,
    error: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    leadingIcon: Painter? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onNext: () -> Unit = {},
    isPasswordToggleDisplayed: Boolean = keyboardType == KeyboardType.Password,
    showPasswordToggle: Boolean = false,
    onPasswordToggleClick: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester = FocusRequester()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    if(it.length <= maxLength) {
                        onValueChange(it)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        testTag = TestTags.STANDARD_TEXT_FIELD
                    }
                    .heightIn(min = 45.dp)
                    .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.extraSmall)
                    .focusRequester(focusRequester = focusRequester),
                maxLines = maxLines,
                minLines = minLines,
                singleLine = singleLine,
                textStyle = Typography.labelMedium.withColor(DarkGray),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        onNext()
                    }
                ),
                visualTransformation = if(!showPasswordToggle && isPasswordToggleDisplayed) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if(leadingIcon != null) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                painter = leadingIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(IconSizeSmall)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            if(text.isEmpty()) {
                                Text(
                                    text = hint,
                                    style = Typography.labelMedium.withColor(DarkGray)
                                )
                            }
                            innerTextField()
                        }
                        if(isPasswordToggleDisplayed) {
                            IconButton(onClick = {
                                onPasswordToggleClick(!showPasswordToggle)
                            },
                                modifier = Modifier
                                    .semantics {
                                        testTag = TestTags.PASSWORD_TOGGLE
                                    }) {
                                Icon(
                                    painter = if(showPasswordToggle) {
                                        painterResource(R.drawable.ic_visibility_off)
                                    } else {
                                        painterResource(R.drawable.ic_visibility)
                                    },
                                    contentDescription = if(showPasswordToggle) {
                                        stringResource(id = R.string.password_visible_content_description)
                                    } else {
                                        stringResource(id = R.string.password_hidden_content_description)
                                    },
                                    modifier = Modifier.size(IconSizeSmall),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }
            )
            if(error.isNotEmpty()) {
                Text(
                    text = error,
                    style = Typography.labelSmall.withSize(12.sp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}