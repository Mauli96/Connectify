package com.connectify.android.feature_auth.presentation.password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.R
import com.connectify.android.core.domain.states.PasswordTextFieldState
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.Screen
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_auth.domain.use_case.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val forgotPassword: ForgotPasswordUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PasswordState())
    val state = _state.asStateFlow()

    private val _newPasswordState = MutableStateFlow(PasswordTextFieldState())
    val newPasswordState = _newPasswordState.asStateFlow()

    private val _confirmPasswordState = MutableStateFlow(PasswordTextFieldState())
    val confirmPasswordState = _confirmPasswordState.asStateFlow()

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        val email = savedStateHandle.get<String>("email") ?: ""
        _state.update {
            it.copy(
                email = email
            )
        }
    }

    fun onEvent(event: PasswordEvent) {
        when(event) {
            is PasswordEvent.OnEnteredNewPassword -> {
                _newPasswordState.update {
                    it.copy(
                        text = event.password
                    )
                }
            }
            is PasswordEvent.OnConfirmPassword -> {
                _confirmPasswordState.update {
                    it.copy(
                        text = event.password
                    )
                }
            }
            is PasswordEvent.OnToggleNewPasswordVisibility -> {
                _newPasswordState.update {
                    it.copy(
                        isPasswordVisible = !newPasswordState.value.isPasswordVisible
                    )
                }
            }
            is PasswordEvent.OnToggleConfirmPasswordVisibility -> {
                _confirmPasswordState.update {
                    it.copy(
                        isPasswordVisible = !confirmPasswordState.value.isPasswordVisible
                    )
                }
            }
            is PasswordEvent.OnPasswordChanged -> {
                changePassword()
            }
        }
    }

    private fun changePassword() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUpdatingPassword = true
                )
            }
            _newPasswordState.update {
                it.copy(
                    error = null
                )
            }
            _confirmPasswordState.update {
                it.copy(
                    error = null
                )
            }
            val passwordResult = forgotPassword(
                email = state.value.email,
                newPassword = newPasswordState.value.text,
                confirmPassword = confirmPasswordState.value.text
            )
            if(passwordResult.newPasswordError != null) {
                _newPasswordState.update {
                    it.copy(
                        error = passwordResult.newPasswordError
                    )
                }
            }
            if(passwordResult.confirmPasswordError != null) {
                _confirmPasswordState.update {
                    it.copy(
                        error = passwordResult.confirmPasswordError
                    )
                }
            }
            when(passwordResult.result) {
                is Resource.Success -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            UiText.StringResource(R.string.password_changed_successfully)
                        )
                    )
                    _eventFlow.send(UiEvent.Navigate(Screen.LoginScreen.route))
                    _state.update {
                        it.copy(
                            isUpdatingPassword = false
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(
                        UiEvent.ShowSnackbar(
                            uiText = passwordResult.result.uiText ?: UiText.unknownError()
                        )
                    )
                    _state.update {
                        it.copy(
                            isUpdatingPassword = false
                        )
                    }
                }
                null -> {
                    _state.update {
                        it.copy(
                            isUpdatingPassword = false
                        )
                    }
                }
            }
        }
    }
}