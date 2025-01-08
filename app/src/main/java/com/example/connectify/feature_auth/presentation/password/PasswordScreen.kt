package com.example.connectify.feature_auth.presentation.password

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
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
import com.example.connectify.feature_auth.presentation.util.AuthError

@Composable
fun PasswordScreen(
    viewModel: PasswordViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val passwordState1 by viewModel.passwordState1.collectAsStateWithLifecycle()
    val passwordState2 by viewModel.passwordState2.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = SpaceLarge),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.password),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        StandardTextField(
            text = passwordState1.text,
            onValueChange = {
                viewModel.onEvent(PasswordEvent.EnteredPassword1(it))
            },
            hint = stringResource(id = R.string.enter_new_password),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onNext = {
            },
            error = when(passwordState1.error) {
                is AuthError.FieldEmpty -> {
                    stringResource(id = R.string.this_field_cant_be_empty)
                }
                else -> ""
            },
            leadingIcon = painterResource(id = R.drawable.ic_password),
            showPasswordToggle = passwordState1.isPasswordVisible,
            onPasswordToggleClick = {
                viewModel.onEvent(PasswordEvent.TogglePasswordVisibility1)
            }
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        StandardTextField(
            text = passwordState2.text,
            onValueChange = {
                viewModel.onEvent(PasswordEvent.EnteredPassword2(it))
            },
            hint = stringResource(id = R.string.confirm_password),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onNext = {
            },
            error = when(passwordState2.error) {
                is AuthError.FieldEmpty -> {
                    stringResource(id = R.string.this_field_cant_be_empty)
                }
                else -> ""
            },
            leadingIcon = painterResource(id = R.drawable.ic_password),
            showPasswordToggle = passwordState2.isPasswordVisible,
            onPasswordToggleClick = {
                viewModel.onEvent(PasswordEvent.TogglePasswordVisibility2)
            }
        )
        Spacer(modifier = Modifier.height(SpaceMedium))
        Button(
            onClick = {
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
                    text = stringResource(id = R.string.change_password),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}