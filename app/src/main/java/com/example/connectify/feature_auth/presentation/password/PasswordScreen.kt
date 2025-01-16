package com.example.connectify.feature_auth.presentation.password

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.connectify.R
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.ui.theme.withSize
import com.example.connectify.core.presentation.util.ObserveAsEvents
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Constants
import com.example.connectify.feature_auth.presentation.util.AuthError
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PasswordScreen(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: PasswordViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val passwordState1 by viewModel.newPasswordState.collectAsStateWithLifecycle()
    val passwordState2 by viewModel.confirmPasswordState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when(event) {
            is UiEvent.ShowSnackbar -> {
                keyboardController?.hide()
                snackbarHostState.showSnackbar(
                    message = event.uiText.asString(context)
                )
            }
            is UiEvent.Navigate -> {
                keyboardController?.hide()
                onNavigate(event.route)
            }
            else -> null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardToolbar(
            onNavigateUp = onNavigateUp,
            showBackArrow = true,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = SpaceLarge,
                    end = SpaceLarge,
                    top = 90.dp
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.connectify),
                modifier = Modifier.size(70.dp)
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            Text(
                text = stringResource(id = R.string.change_password),
                style = Typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
            StandardTextField(
                text = passwordState1.text,
                onValueChange = {
                    viewModel.onEvent(PasswordEvent.OnEnteredNewPassword(it))
                },
                hint = stringResource(id = R.string.enter_new_password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                error = when(passwordState1.error) {
                    is AuthError.FieldEmpty -> {
                        stringResource(id = R.string.this_field_cant_be_empty)
                    }
                    is AuthError.InputTooShort -> {
                        stringResource(id = R.string.input_too_short, Constants.MIN_PASSWORD_LENGTH)
                    }
                    is AuthError.InvalidPassword -> {
                        stringResource(id = R.string.invalid_password)
                    }
                    else -> ""
                },
                leadingIcon = painterResource(id = R.drawable.ic_password),
                showPasswordToggle = passwordState1.isPasswordVisible,
                onPasswordToggleClick = {
                    viewModel.onEvent(PasswordEvent.OnToggleNewPasswordVisibility)
                }
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardTextField(
                text = passwordState2.text,
                onValueChange = {
                    viewModel.onEvent(PasswordEvent.OnConfirmPassword(it))
                },
                hint = stringResource(id = R.string.confirm_password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onNext = {
                    focusManager.clearFocus()
                },
                error = when(passwordState2.error) {
                    is AuthError.FieldEmpty -> {
                        stringResource(id = R.string.this_field_cant_be_empty)
                    }
                    is AuthError.InputTooShort -> {
                        stringResource(id = R.string.input_too_short, Constants.MIN_PASSWORD_LENGTH)
                    }
                    is AuthError.InvalidPassword -> {
                        stringResource(id = R.string.invalid_password)
                    }
                    is AuthError.PasswordsDoNotMatch -> {
                        stringResource(id = R.string.password_did_not_matched)
                    }
                    else -> ""
                },
                leadingIcon = painterResource(id = R.drawable.ic_password),
                showPasswordToggle = passwordState2.isPasswordVisible,
                onPasswordToggleClick = {
                    viewModel.onEvent(PasswordEvent.OnToggleConfirmPasswordVisibility)
                }
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            Button(
                onClick = {
                    viewModel.onEvent(PasswordEvent.OnPasswordChanged)
                },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            ) {
                if(state.isUpdatingPassword) {
                    CustomCircularProgressIndicator(
                        modifier = Modifier
                            .size(IconSizeSmall)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.change_password),
                        style = Typography.labelMedium
                            .withSize(15.sp)
                            .withColor(DarkGray)
                    )
                }
            }
        }
    }
}