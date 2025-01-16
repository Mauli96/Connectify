package com.example.connectify.feature_auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.PasswordTextFieldState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_auth.domain.models.SignInResult
import com.example.connectify.feature_auth.domain.use_case.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _emailState = MutableStateFlow(StandardTextFieldState())
    val emailState = _emailState.asStateFlow()

    private val _passwordState = MutableStateFlow(PasswordTextFieldState())
    val passwordState = _passwordState.asStateFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.OnEnteredEmail -> {
                _emailState.update {
                    it.copy(
                        text = event.email
                    )
                }
            }
            is LoginEvent.OnEnteredPassword -> {
                _passwordState.update {
                    it.copy(
                        text = event.password
                    )
                }
            }
            is LoginEvent.OnSignIn -> {
                when(event.result) {
                    is SignInResult.Success -> {
                        _emailState.update {
                            it.copy(
                                text = event.result.email
                            )
                        }
                        _passwordState.update {
                            it.copy(
                                text = event.result.password
                            )
                        }
                        login()
                    }
                    is SignInResult.Cancelled -> {
                        viewModelScope.launch {
                            _eventFlow.send(
                                UiEvent.ShowSnackbar(
                                    UiText.StringResource(R.string.login_canceled)
                                )
                            )
                        }
                    }
                    is SignInResult.Failure -> {
                        viewModelScope.launch {
                            _eventFlow.send(
                                UiEvent.ShowSnackbar(
                                    UiText.StringResource(R.string.login_failed)
                                )
                            )
                        }
                    }
                    is SignInResult.NoCredentials -> {
                        viewModelScope.launch {
                            _eventFlow.send(
                                UiEvent.ShowSnackbar(
                                    UiText.StringResource(R.string.no_credentials)
                                )
                            )
                        }
                    }
                }
            }
            is LoginEvent.OnTogglePasswordVisibility -> {
                _passwordState.update {
                    it.copy(
                        isPasswordVisible = !passwordState.value.isPasswordVisible
                    )
                }
            }
            is LoginEvent.OnLogin -> {
                login()
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _emailState.update {
                it.copy(error = null)
            }
            _passwordState.update {
                it.copy(error = null)
            }
            _loginState.update {
                it.copy(isLoading = true)
            }
            val loginResult = loginUseCase(
                email = emailState.value.text,
                password = passwordState.value.text
            )
            if(loginResult.emailError != null) {
                _emailState.update {
                    it.copy(
                        error = loginResult.emailError
                    )
                }
            }
            if(loginResult.passwordError != null) {
                _passwordState.update {
                    it.copy(
                        error = loginResult.passwordError
                    )
                }
            }
            when(loginResult.result) {
                is Resource.Success -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            UiText.StringResource(R.string.success_login)
                        )
                    )
                    _eventFlow.send(UiEvent.OnLogin)
                    _loginState.update {
                        it.copy(isLoading = false)
                    }
                    _emailState.value = StandardTextFieldState()
                    _passwordState.value = PasswordTextFieldState()
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = loginResult.result.uiText ?: UiText.unknownError()
                        )
                    )
                    _loginState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
                null -> {
                    _loginState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}