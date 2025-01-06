package com.example.connectify.feature_auth.presentation.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.connectify.R
import com.example.connectify.core.presentation.components.StandardTextField
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.feature_auth.presentation.login.LoginEvent
import com.example.connectify.feature_auth.presentation.otp.component.OtpInputField
import com.example.connectify.feature_auth.presentation.util.AuthError

@Composable
fun OtpScreen(
    viewModel: OtpViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val emailState by viewModel.emailState.collectAsStateWithLifecycle()

    val focusRequesters = remember {
        List(4) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.focusedIndex) {
        state.focusedIndex?.let { index ->
            focusRequesters.getOrNull(index)?.requestFocus()
        }
    }

    LaunchedEffect(state.code, keyboardManager) {
        val allNumbersEntered = state.code.none { it == null }
        if(allNumbersEntered) {
            focusRequesters.forEach {
                it.freeFocus()
            }
            focusManager.clearFocus()
            keyboardManager?.hide()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(state.showEmailInput) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(SpaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StandardTextField(
                    text = emailState.text,
                    onValueChange = {
                        viewModel.onEvent(OtpEvent.EnteredEmail(it))
                    },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    error = when(emailState.error) {
                        is AuthError.FieldEmpty -> {
                            stringResource(id = R.string.this_field_cant_be_empty)
                        }
                        else -> ""
                    },
                    leadingIcon = painterResource(id = R.drawable.ic_email),
                    hint = stringResource(id = R.string.email_hint)
                )
                Button(
                    onClick = {
                        viewModel.onEvent(OtpEvent.OnVerifyEmail)
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .align(CenterVertically)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.verify_email),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .padding(SpaceMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterHorizontally)
            ) {
                state.code.forEachIndexed { index, number ->
                    OtpInputField(
                        number = number,
                        focusRequester = focusRequesters[index],
                        onFocusChanged = { isFocused ->
                            if(isFocused) {
                                viewModel.onEvent(OtpEvent.OnChangeFieldFocused(index))
                            }
                        },
                        onNumberChanged = { newNumber ->
                            viewModel.onEvent(OtpEvent.OnEnterNumber(newNumber, index))
                        },
                        onKeyboardBack = {
                            viewModel.onEvent(OtpEvent.OnKeyboardBack)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }

            state.isValid?.let { isValid ->
                Text(
                    text = if(isValid) "OTP is valid!" else "OTP is invalid!",
                    style = MaterialTheme.typography.bodySmall,
                    color = if(isValid) {
                        MaterialTheme.colorScheme.primary
                    } else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}