package com.connectify.android.feature_auth.presentation.otp

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.FocusRequester
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.PulsatingLoadingText
import com.connectify.android.core.presentation.components.StandardTextField
import com.connectify.android.core.presentation.components.StandardToolbar
import com.connectify.android.core.presentation.ui.theme.DarkGray
import com.connectify.android.core.presentation.ui.theme.IconSizeMedium
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
import com.connectify.android.feature_auth.presentation.otp.component.OtpInputField
import com.connectify.android.feature_auth.presentation.util.AuthError

@Composable
fun OtpScreen(
    snackbarHostState: SnackbarHostState,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: OtpViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val emailState by viewModel.emailState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val focusRequesters = remember {
        List(4) { FocusRequester() }
    }

    val composition1 by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))
    val progress1 by animateLottieCompositionAsState(
        composition = composition1,
        iterations = 1
    )

    val composition2 by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
    val progress2 by animateLottieCompositionAsState(
        composition = composition2,
        iterations = 1
    )

    LaunchedEffect(state.focusedIndex) {
        state.focusedIndex?.let { index ->
            focusRequesters.getOrNull(index)?.requestFocus()
        }
    }

    LaunchedEffect(state.code, keyboardController) {
        val allNumbersEntered = state.code.none { it == null }
        if(allNumbersEntered) {
            focusRequesters.forEach {
                it.freeFocus()
            }
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

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
                .padding(top = 120.dp),
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
                text = stringResource(id = R.string.verify_your_email),
                style = Typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
            if(state.showEmailInput) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceLarge),
                    verticalArrangement = Arrangement.spacedBy(SpaceMedium)
                ) {
                    StandardTextField(
                        text = emailState.text,
                        onValueChange = {
                            viewModel.onEvent(OtpEvent.OnEnteredEmail(it))
                        },
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                        error = when(emailState.error) {
                            is AuthError.FieldEmpty -> {
                                stringResource(id = R.string.this_field_cant_be_empty)
                            }
                            is AuthError.InvalidEmail -> {
                                stringResource(id = R.string.not_a_valid_email)
                            }
                            else -> ""
                        },
                        leadingIcon = painterResource(id = R.drawable.ic_email),
                        hint = stringResource(id = R.string.email_hint)
                    )
                    Button(
                        onClick = {
                            viewModel.onEvent(OtpEvent.OnSendOtp)
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(state.isOtpGenerating) {
                            CustomCircularProgressIndicator(
                                modifier = Modifier
                                    .size(IconSizeSmall)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.verify_email),
                                style = Typography.labelMedium
                                    .withSize(15.sp)
                                    .withColor(DarkGray)
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .padding(
                            vertical = SpaceLarge,
                            horizontal = SpaceMedium
                        ),
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

                if(state.isOtpVerifying) {
                    PulsatingLoadingText(
                        text = stringResource(R.string.otp_verifying)
                    )
                } else {
                    state.isValid?.let { isValid ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if(isValid) {
                                LottieAnimation(
                                    modifier = Modifier.size(IconSizeMedium),
                                    composition = composition1,
                                    progress = { progress1 }
                                )
                            } else {
                                LottieAnimation(
                                    modifier = Modifier.size(IconSizeMedium),
                                    composition = composition2,
                                    progress = { progress2 }
                                )
                            }
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = if(isValid) {
                                    stringResource(R.string.otp_valid)
                                } else stringResource(R.string.otp_invalid),
                                style = MaterialTheme.typography.labelSmall,
                                color = if(isValid) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}