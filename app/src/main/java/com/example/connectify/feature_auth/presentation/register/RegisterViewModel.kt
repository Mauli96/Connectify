package com.example.connectify.feature_auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.domain.states.PasswordTextFieldState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.Screen
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_auth.domain.use_case.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _emailState = MutableStateFlow(StandardTextFieldState())
    val emailState = _emailState.asStateFlow()

    private val _usernameState = MutableStateFlow(StandardTextFieldState())
    val usernameState = _usernameState.asStateFlow()

    private val _passwordState = MutableStateFlow(PasswordTextFieldState())
    val passwordState = _passwordState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.EnteredUsername -> {
                _usernameState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is RegisterEvent.EnteredEmail -> {
                _emailState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is RegisterEvent.EnteredPassword -> {
                _passwordState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _passwordState.update {
                    it.copy(
                        isPasswordVisible = !passwordState.value.isPasswordVisible
                    )
                }
            }
            is RegisterEvent.Register -> {
                register()
            }
        }
    }

    private fun register() {
        viewModelScope.launch {
            _usernameState.value = usernameState.value.copy(error = null)
            _emailState.value = emailState.value.copy(error = null)
            _passwordState.value = passwordState.value.copy(error = null)
            _registerState.value = RegisterState(isLoading = true)

            val registerResult = registerUseCase(
                email = emailState.value.text,
                username = usernameState.value.text,
                password = passwordState.value.text
            )
            if(registerResult.emailError != null) {
                _emailState.update {
                    it.copy(
                        error = registerResult.emailError
                    )
                }
            }
            if(registerResult.usernameError != null) {
                _usernameState.update {
                    it.copy(
                        error = registerResult.usernameError
                    )
                }
            }
            if(registerResult.passwordError != null) {
                _passwordState.update {
                    it.copy(
                        error = registerResult.passwordError
                    )
                }
            }
            when(registerResult.result) {
                is Resource.Success -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            UiText.StringResource(R.string.success_registeration)
                        )
                    )
                    _eventFlow.emit(UiEvent.Navigate(Screen.LoginScreen.route))
                    _registerState.value = RegisterState(isLoading = false)
                    _usernameState.value = StandardTextFieldState()
                    _emailState.value = StandardTextFieldState()
                    _passwordState.value = PasswordTextFieldState()
                }
                is Resource.Error -> {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            uiText = registerResult.result.uiText ?: UiText.unknownError()
                        )
                    )
                    _registerState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
                null -> {
                    _registerState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}