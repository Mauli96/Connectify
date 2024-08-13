package com.example.connectify.feature_auth.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.domain.states.PasswordTextFieldState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_auth.domain.use_case.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _emailState = mutableStateOf(StandardTextFieldState())
    val emailState: State<StandardTextFieldState> = _emailState

    private val _passwordState = mutableStateOf(PasswordTextFieldState())
    val passwordState: State<PasswordTextFieldState> = _passwordState

    private val _loginState = mutableStateOf(LoginState())
    val loginState: State<LoginState> = _loginState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.EnteredEmail -> {
                _emailState.value = emailState.value.copy(
                    text = event.email
                )
            }
            is LoginEvent.EnteredPassword -> {
                _passwordState.value = passwordState.value.copy(
                    text = event.password
                )
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _passwordState.value = passwordState.value.copy(
                    isPasswordVisible = !passwordState.value.isPasswordVisible
                )
            }
            is LoginEvent.Login -> {
                viewModelScope.launch {
                    _emailState.value = emailState.value.copy(error = null)
                    _passwordState.value = passwordState.value.copy(error = null)
                    _loginState.value = loginState.value.copy(isLoading = true)
                    val loginResult = loginUseCase(
                        email = emailState.value.text,
                        password = passwordState.value.text
                    )
                    if(loginResult.emailError != null) {
                        _emailState.value = emailState.value.copy(
                            error = loginResult.emailError
                        )
                    }
                    if(loginResult.passwordError != null) {
                        _passwordState.value = _passwordState.value.copy(
                            error = loginResult.passwordError
                        )
                    }
                    when(loginResult.result) {
                        is Resource.Success -> {
                            UiEvent.ShowSnackbar(
                                UiText.StringResource(R.string.success_login)
                            )
                            _eventFlow.emit(UiEvent.OnLogin)
                            _loginState.value = loginState.value.copy(isLoading = false)
                            _emailState.value = StandardTextFieldState()
                            _passwordState.value = PasswordTextFieldState()
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    uiText = loginResult.result.uiText ?: UiText.unknownError()
                                )
                            )
                            _loginState.value = loginState.value.copy(isLoading = false)
                        }
                        null -> {
                            _loginState.value = loginState.value.copy(isLoading = false)
                        }
                    }
                }
            }
        }
    }
}