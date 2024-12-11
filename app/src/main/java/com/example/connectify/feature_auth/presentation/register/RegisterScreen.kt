package com.example.connectify.feature_auth.presentation.register

import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.connectify.R
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.StandardTextField
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_auth.data.credential_manager.AccountManager
import com.example.connectify.feature_auth.presentation.util.AuthError
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun RegisterScreen(
    onNavigate: (String) -> Unit = {},
    snackbarHostState: SnackbarHostState,
    onRegister: () -> Unit = {},
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val usernameState by viewModel.usernameState.collectAsStateWithLifecycle()
    val emailState by viewModel.emailState.collectAsStateWithLifecycle()
    val passwordState by viewModel.passwordState.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val accountManager = remember {
        AccountManager(context as ComponentActivity)
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    keyboardController?.hide()
                    GlobalScope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.uiText.asString(context)
                        )
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                is UiEvent.OnRegister -> {
                    onRegister()
                    accountManager.signUp(
                        email = emailState.text,
                        password = passwordState.text
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = SpaceLarge,
                    end = SpaceLarge,
                    top = SpaceLarge,
                    bottom = 50.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.register),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                StandardTextField(
                    text = emailState.text,
                    onValueChange = {
                        viewModel.onEvent(RegisterEvent.EnteredEmail(it))
                    },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    leadingIcon = painterResource(id = R.drawable.ic_email),
                    error = when(emailState.error) {
                        is AuthError.FieldEmpty -> {
                            stringResource(id = R.string.this_field_cant_be_empty)
                        }
                        is AuthError.InvalidEmail -> {
                            stringResource(id = R.string.not_a_valid_email)
                        }
                        else -> ""
                    },
                    hint = stringResource(id = R.string.email)
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                StandardTextField(
                    text = usernameState.text,
                    onValueChange = {
                        viewModel.onEvent(RegisterEvent.EnteredUsername(it))
                    },
                    imeAction = ImeAction.Next,
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                    error = when(usernameState.error) {
                        is AuthError.FieldEmpty -> {
                            stringResource(id = R.string.this_field_cant_be_empty)
                        }
                        is AuthError.InputTooShort -> {
                            stringResource(id = R.string.input_too_short, Constants.MIN_USERNAME_LENGTH)
                        }
                        else -> ""
                    },
                    leadingIcon = painterResource(id = R.drawable.ic_person),
                    hint = stringResource(id = R.string.username)
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                StandardTextField(
                    text = passwordState.text,
                    onValueChange = {
                        viewModel.onEvent(RegisterEvent.EnteredPassword(it))
                    },
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    onNext = {
                        focusManager.clearFocus()
                    },
                    hint = stringResource(id = R.string.password_hint),
                    error = when(passwordState.error) {
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
                    showPasswordToggle = passwordState.isPasswordVisible,
                    onPasswordToggleClick = {
                        viewModel.onEvent(RegisterEvent.TogglePasswordVisibility)
                    }
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                Button(
                    onClick = {
                        viewModel.onEvent(RegisterEvent.Register)
                    },
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(registerState.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(20.dp)
                                .align(CenterVertically)
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.register),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.already_have_an_account))
                    append(" ")
                    val signUpText = stringResource(id = R.string.sign_in)
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append(signUpText)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onNavigate(Screen.LoginScreen.route)
                            }
                        )
                    }
            )
        }
        ConnectivityBanner(
            networkState = networkState,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}