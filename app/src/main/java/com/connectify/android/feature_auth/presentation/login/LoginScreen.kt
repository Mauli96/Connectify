package com.connectify.android.feature_auth.presentation.login

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.connectify.android.R
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.StandardTextField
import com.connectify.android.core.presentation.ui.theme.DarkGray
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.SpaceLarge
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.util.Screen
import com.connectify.android.feature_auth.data.credential_manager.AccountManager
import com.connectify.android.feature_auth.presentation.util.AuthError

@Composable
fun LoginScreen(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val emailState by viewModel.emailState.collectAsStateWithLifecycle()
    val passwordState by viewModel.passwordState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val accountManager = remember {
        AccountManager(context as ComponentActivity)
    }

    LaunchedEffect(true) {
        val result = accountManager.signIn()
        viewModel.onEvent(LoginEvent.OnSignIn(result))
    }

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                keyboardController?.hide()
                snackbarHostState.showSnackbar(event.uiText.asString(context))
            }

            is UiEvent.OnLogin -> {
                keyboardController?.hide()
                onLogin()
            }

            else -> Unit
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
                .padding(horizontal = SpaceLarge)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(140.dp))

            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.connectify),
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            Text(
                text = stringResource(id = R.string.login_to_connectify),
                style = Typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(SpaceLarge))

            StandardTextField(
                text = emailState.text,
                onValueChange = { viewModel.onEvent(LoginEvent.OnEnteredEmail(it)) },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                error = when (emailState.error) {
                    is AuthError.FieldEmpty -> stringResource(id = R.string.this_field_cant_be_empty)
                    else -> ""
                },
                leadingIcon = painterResource(id = R.drawable.ic_email),
                hint = stringResource(id = R.string.email_hint)
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            StandardTextField(
                text = passwordState.text,
                onValueChange = { viewModel.onEvent(LoginEvent.OnEnteredPassword(it)) },
                hint = stringResource(id = R.string.password),
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onNext = { focusManager.clearFocus() },
                error = when (passwordState.error) {
                    is AuthError.FieldEmpty -> stringResource(id = R.string.this_field_cant_be_empty)
                    else -> ""
                },
                leadingIcon = painterResource(id = R.drawable.ic_password),
                showPasswordToggle = passwordState.isPasswordVisible,
                onPasswordToggleClick = { viewModel.onEvent(LoginEvent.OnTogglePasswordVisibility) }
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = stringResource(id = R.string.forgot_password),
                style = Typography.labelMedium.withSize(14.sp),
                modifier = Modifier
                    .align(Alignment.End)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onNavigate(Screen.OtpScreen.route)
                        }
                    }
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Button(
                onClick = { viewModel.onEvent(LoginEvent.OnLogin) },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CustomCircularProgressIndicator(
                        modifier = Modifier.size(IconSizeSmall)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.login),
                        style = Typography.labelMedium
                            .withSize(15.sp)
                            .withColor(DarkGray)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = R.string.dont_have_an_account_yet))
                    append(" ")
                    val signUpText = stringResource(id = R.string.sign_up)
                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        append(signUpText)
                    }
                },
                style = Typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = SpaceMedium)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            onNavigate(Screen.RegisterScreen.route)
                        }
                    }
            )
        }

        ConnectivityBanner(
            networkState = networkState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}